package com.yeoljeong.tripmate.order.application.dto.result;

import com.yeoljeong.tripmate.order.domain.entity.Order;
import com.yeoljeong.tripmate.order.domain.entity.OrderItem;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record CreateOrderResult(
        UUID orderId,
        UUID userId,
        String orderStatus,
        List<OrderItemResult> orderItems,
        LocalDateTime cancelledAt,
        String cancelReason
) {
    public static CreateOrderResult from(Order order) {
        return CreateOrderResult.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .orderStatus(order.getOrderStatus().name())
                .orderItems(order.getOrderItems().stream()
                        .map(OrderItemResult::from)
                        .toList())
                .cancelledAt(order.getCancelledAt())
                .cancelReason(order.getCancelReason())
                .build();
    }

    @Builder
    public record OrderItemResult(
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
        public static OrderItemResult from(OrderItem orderItem) {
            return OrderItemResult.builder()
                    .orderItemId(orderItem.getId())
                    .planUnitId(orderItem.getPlanUnitId())
                    .productId(orderItem.getProductInfo().getProductId())
                    .productName(orderItem.getProductInfo().getProductName())
                    .price(orderItem.getProductInfo().getPrice())
                    .companyName(orderItem.getProductInfo().getCompanyName())
                    .country(orderItem.getProductInfo().getCountry().name())
                    .state(orderItem.getProductInfo().getState())
                    .city(orderItem.getProductInfo().getCity())
                    .scheduleId(orderItem.getProductInfo().getScheduleId())
                    .quantity(orderItem.getQuantity())
                    .experienceDate(orderItem.getExperienceDate())
                    .build();
        }
    }
}
