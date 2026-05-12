package com.yeoljeong.tripmate.order.application.service.command;

import com.yeoljeong.tripmate.event.OrderCancelledEvent;
import com.yeoljeong.tripmate.event.OrderCreatedEvent;
import com.yeoljeong.tripmate.event.OrderSchedulerCancelledEvent;
import com.yeoljeong.tripmate.event.enums.OrderTopic;
import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.order.application.client.PaymentClient;
import com.yeoljeong.tripmate.order.application.client.PlanClient;
import com.yeoljeong.tripmate.order.application.client.ProductClient;
import com.yeoljeong.tripmate.order.application.dto.command.ApprovalUserCommand;
import com.yeoljeong.tripmate.order.application.dto.command.CreateOrderCommand;
import com.yeoljeong.tripmate.order.application.dto.command.OrderableProductCommand;
import com.yeoljeong.tripmate.order.application.dto.result.DeletableOrderResult;
import com.yeoljeong.tripmate.order.application.dto.result.OrderResult;
import com.yeoljeong.tripmate.order.application.port.OrderOutboxRecorder;
import com.yeoljeong.tripmate.order.domain.enums.OrderCancelReason;
import com.yeoljeong.tripmate.order.domain.enums.OrderStatus;
import com.yeoljeong.tripmate.order.domain.exception.OrderErrorCode;
import com.yeoljeong.tripmate.order.domain.model.Order;
import com.yeoljeong.tripmate.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final PlanClient planClient;
    private final PaymentClient paymentClient;
    private final OrderOutboxRecorder orderOutboxRecorder;

    private static final long PAYMENT_TIMEOUT_MINUTES = 15;

    public OrderResult createOrder(CreateOrderCommand orderCommand) {

        if (orderCommand.orderItems() == null || orderCommand.orderItems().size() != 1) {
            throw new BusinessException(OrderErrorCode.INVALID_ORDER_ITEM_COUNT);
        }

        CreateOrderCommand.OrderItemCommand orderItemCommand = orderCommand.orderItems().get(0);

        // 참여 여부 조회
        ApprovalUserCommand approvalUserCommand = planClient.getPlanParticipation(orderCommand.userId(), orderItemCommand.planUnitId());

        // 참여 가능 상태인지 검증
        validateParticipationAvailable(approvalUserCommand.status());

        // 상품 정보 조회
        OrderableProductCommand productCommand = productClient.getSchedule(orderItemCommand.productId(), orderItemCommand.scheduleId());

        // 이미 구매한 단위 일정의 상품인지 확인
        validateDuplicateOrder(orderCommand.userId(), orderItemCommand.planUnitId());

        // 판매 가능 상태인지 검증
        validateProductAvailable(productCommand.productStatus());
        validateScheduleAvailable(productCommand.scheduleStatus());
        validateStock(productCommand.stock(), orderItemCommand.quantity());

        Order order = Order.create(
                orderCommand.userId(),
                orderItemCommand.planUnitId(),
                productCommand.productId(),
                productCommand.productName(),
                productCommand.price(),
                productCommand.country(),
                productCommand.state(),
                productCommand.city(),
                productCommand.productScheduleId(),
                orderItemCommand.quantity(),
                productCommand.date(),
                LocalDate.now()
        );

        Order savedOrder = orderRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                savedOrder.getUserId(),
                savedOrder.getId(),
                savedOrder.getOrderItems().get(0).getPlanUnitId(),
                savedOrder.getOrderItems().get(0).getProductInfo().getProductId(),
                savedOrder.getOrderItems().get(0).getProductInfo().getScheduleId(),
                savedOrder.getOrderItems().get(0).getQuantity()
        );

        // 주문 생성 이벤트 outbox에 저장
        orderOutboxRecorder.record(OrderTopic.ORDER_CREATED_TOPIC, event);

        return OrderResult.from(savedOrder);
    }

    // 결제 완료 이벤트 수신 후 동작
    public void completePayment(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));

        if (order.isCompleted()) {
            return;
        }

        order.complete();
    }

    // 일정 탈퇴 이벤트 수신 후 동작
    public void cancelOrderByParticipantQuit(UUID userId, UUID planUnitId, OrderCancelReason reason) {
        Order order = orderRepository.findByUserIdAndPlanUnitId(userId, planUnitId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));

        if (order.isCancelled()) {
            return;
        }

        order.cancel(LocalDateTime.now(), reason);

        OrderCancelledEvent event = new OrderCancelledEvent(
                UUID.randomUUID(),
                order.getId(),
                order.getUserId(),
                order.getOrderItems().get(0).getPlanUnitId(),
                reason.getDescription(),
                order.getOrderItems().get(0).getProductInfo().getProductId(),
                order.getOrderItems().get(0).getProductInfo().getProductName(),
                order.getOrderItems().get(0).getProductInfo().getScheduleId(),
                order.getOrderItems().get(0).getQuantity()
        );

        // 주문 취소 이벤트 outbox에 저장
        orderOutboxRecorder.record(OrderTopic.ORDER_CANCELLED_TOPIC, event);
    }

    // 인원 증가 실패 / 인원 감소 이벤트 수신 후 동작
    public void cancelOrderByPlanUnitParticipantRollback(UUID orderId, OrderCancelReason reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));

        if (order.isCancelled()) {
            return;
        }

        order.cancel(LocalDateTime.now(), reason);
    }

    // 주문 취소 15분 후 스케줄러 작동 로직
    public void cancelTimeoutOrders(int batchSize) {
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(PAYMENT_TIMEOUT_MINUTES);

        Slice<Order> timeoutOrders = orderRepository.findAllByOrderStatusAndCreatedAtBefore(
                OrderStatus.CREATED, timeoutThreshold, PageRequest.of(0, batchSize));

        for (Order order : timeoutOrders) {
            try {
                DeletableOrderResult payment = paymentClient.getDeletablePayment(order.getId());

                if (!payment.exists()) {
                    order.cancel(LocalDateTime.now(), OrderCancelReason.PAYMENT_TIMEOUT);

                    OrderSchedulerCancelledEvent event = new OrderSchedulerCancelledEvent(
                            UUID.randomUUID(),
                            order.getUserId(),
                            order.getOrderItems().get(0).getPlanUnitId(),
                            order.getOrderItems().get(0).getProductInfo().getProductId(),
                            order.getOrderItems().get(0).getProductInfo().getScheduleId(),
                            order.getOrderItems().get(0).getQuantity()
                    );

                    // 스케줄러 주문 취소 이벤트 outbox에 저장
                    orderOutboxRecorder.record(OrderTopic.ORDER_SCHEDULER_CANCELLED_TOPIC, event);
                }
            } catch (Exception e) {
                log.warn("[Order] timeout cancel skip: orderId={}", order.getId(), e);
            }
        }
    }

    private void validateParticipationAvailable(String participationStatus) {
        if (!"APPROVED".equals(participationStatus)) {
            throw new BusinessException(OrderErrorCode.PLAN_PARTICIPATION_NOT_AVAILABLE);
        }
    }

    private void validateProductAvailable(String productStatus) {
        if (!"ACTIVE".equals(productStatus)) {
            throw new BusinessException(OrderErrorCode.PRODUCT_NOT_AVAILABLE);
        }
    }

    private void validateScheduleAvailable(String scheduleStatus) {
        if (!"ACTIVE".equals(scheduleStatus)) {
            throw new BusinessException(OrderErrorCode.SCHEDULE_NOT_AVAILABLE);
        }
    }

    private void validateStock(Integer stock, Integer quantity) {
        if (quantity == null || quantity != 1) {
            throw new BusinessException(OrderErrorCode.INVALID_QUANTITY);
        }
        if (stock == null || stock < quantity) {
            throw new BusinessException(OrderErrorCode.INSUFFICIENT_STOCK);
        }
    }

    private void validateDuplicateOrder(UUID userId, UUID planUnitId) {
        if (orderRepository.existsByUserIdAndPlanUnitIdAndOrderStatusIn(userId, planUnitId,
                List.of(OrderStatus.CREATED, OrderStatus.COMPLETED))) {
            throw new BusinessException(OrderErrorCode.ALREADY_ORDERED_PLAN_UNIT);
        }
    }
}
