package com.yeoljeong.tripmate.presentation.dto.response;

import com.yeoljeong.tripmate.application.dto.result.CreateOrderResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID orderId,
        UUID userId,
        String orderStatus,
        List<OrderItemResponse> orderItems,
        LocalDateTime cancelledAt,
        String cancelReason
) {
    public static OrderResponse from(CreateOrderResult result) {
        return new OrderResponse(
                result.orderId(),
                result.userId(),
                result.orderStatus(),
                result.orderItems().stream()
                        .map(OrderItemResponse::from)
                        .toList(),
                result.cancelledAt(),
                result.cancelReason()
        );
    }

    public record OrderItemResponse(
            UUID orderItemId,
            UUID planUnitId,
            UUID productId,
            String productName,
            BigDecimal price,
            String companyName,
            String country,
            String state,
            String city,
            UUID scheduleId,
            Integer quantity,
            LocalDate experienceDate
    ) {
        public static OrderItemResponse from(CreateOrderResult.OrderItemResult result) {
            return new OrderItemResponse(
                    result.orderItemId(),
                    result.planUnitId(),
                    result.productId(),
                    result.productName(),
                    result.price(),
                    result.companyName(),
                    result.country(),
                    result.state(),
                    result.city(),
                    result.scheduleId(),
                    result.quantity(),
                    result.experienceDate()
            );
        }
    }
}
