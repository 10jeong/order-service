package com.yeoljeong.tripmate.application.service.command;

import com.yeoljeong.tripmate.application.client.ProductClient;
import com.yeoljeong.tripmate.application.dto.command.CreateOrderCommand;
import com.yeoljeong.tripmate.application.dto.command.OrderableProductCommand;
import com.yeoljeong.tripmate.application.dto.result.CreateOrderResult;
import com.yeoljeong.tripmate.domain.entity.Order;
import com.yeoljeong.tripmate.domain.exception.OrderErrorCode;
import com.yeoljeong.tripmate.domain.repository.OrderRepository;
import com.yeoljeong.tripmate.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    public CreateOrderResult creatOrder(CreateOrderCommand orderCommand) {

        CreateOrderCommand.OrderItemCommand orderItemCommand = orderCommand.orderItems().get(0);

        // 상품 정보 조회
        OrderableProductCommand productCommand = productClient.getProduct(orderCommand.userId(), orderItemCommand.scheduleId());

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
                productCommand.companyName(),
                productCommand.country(),
                productCommand.state(),
                productCommand.city(),
                productCommand.productScheduleId(),
                orderItemCommand.quantity(),
                orderItemCommand.experienceDate(),
                LocalDate.now()
        );

        Order savedOrder = orderRepository.save(order);

        return CreateOrderResult.from(savedOrder);
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
        if (stock == null || quantity == null || stock < quantity) {
            throw new BusinessException(OrderErrorCode.INSUFFICIENT_STOCK);
        }
    }

    private void validateDuplicateOrder(UUID userId, UUID planUnitId) {
        if (orderRepository.existsByUserIdAndPlanUnitId(userId, planUnitId)) {
            throw new BusinessException(OrderErrorCode.ALREADY_ORDERED_PLAN_UNIT);
        }
    }
}
