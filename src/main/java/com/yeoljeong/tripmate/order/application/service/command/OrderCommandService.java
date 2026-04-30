package com.yeoljeong.tripmate.order.application.service.command;

import com.yeoljeong.tripmate.order.application.client.PlanClient;
import com.yeoljeong.tripmate.order.application.client.ProductClient;
import com.yeoljeong.tripmate.order.application.dto.command.ApprovalUserCommand;
import com.yeoljeong.tripmate.order.application.dto.command.CreateOrderCommand;
import com.yeoljeong.tripmate.order.application.dto.command.OrderableProductCommand;
import com.yeoljeong.tripmate.order.application.dto.result.OrderResult;
import com.yeoljeong.tripmate.order.domain.model.Order;
import com.yeoljeong.tripmate.order.domain.exception.OrderErrorCode;
import com.yeoljeong.tripmate.order.domain.repository.OrderRepository;
import com.yeoljeong.tripmate.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final PlanClient planClient;

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
        OrderableProductCommand productCommand = productClient.getSchedule(orderCommand.userId(), orderItemCommand.productId(), orderItemCommand.scheduleId());

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

        return OrderResult.from(savedOrder);
    }

    private void validateParticipationAvailable(String participationStatus) {
        if (!"APPROVAL".equals(participationStatus)) {
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
        if (stock == null || quantity == null || quantity <= 0 || stock < quantity) {
            throw new BusinessException(OrderErrorCode.INSUFFICIENT_STOCK);
        }
    }

    private void validateDuplicateOrder(UUID userId, UUID planUnitId) {
        if (orderRepository.existsByUserIdAndPlanUnitId(userId, planUnitId)) {
            throw new BusinessException(OrderErrorCode.ALREADY_ORDERED_PLAN_UNIT);
        }
    }
}
