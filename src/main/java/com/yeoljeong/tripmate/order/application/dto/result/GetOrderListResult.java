package com.yeoljeong.tripmate.order.application.dto.result;

import com.yeoljeong.tripmate.order.domain.model.Order;
import com.yeoljeong.tripmate.order.domain.model.OrderItem;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record GetOrderListResult(
        UUID orderId,
        UUID userId,
        String orderStatus,
        String productName,
        Integer totalQuantity,
        BigDecimal totalPrice,
        LocalDate experienceDate
) {
    public static GetOrderListResult from(Order order) {
        OrderItem firstOrderItem = order.getOrderItems().get(0);

        int totalQuantity = order.getOrderItems().stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        BigDecimal totalPrice = order.getOrderItems().stream()
                .map(orderItem -> orderItem.getProductInfo().getPrice()
                        .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return GetOrderListResult.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .orderStatus(order.getOrderStatus().name())
                .productName(firstOrderItem.getProductInfo().getProductName())
                .totalQuantity(totalQuantity)
                .totalPrice(totalPrice)
                .experienceDate(firstOrderItem.getExperienceDate())
                .build();
    }
}
