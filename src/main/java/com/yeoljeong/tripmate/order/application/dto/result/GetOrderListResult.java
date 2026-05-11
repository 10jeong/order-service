package com.yeoljeong.tripmate.order.application.dto.result;

import com.yeoljeong.tripmate.order.domain.model.Order;
import com.yeoljeong.tripmate.order.domain.model.OrderItem;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems == null || orderItems.isEmpty()) {
                throw new IllegalStateException("Order has no items. orderId=" + order.getId());
            }
        OrderItem firstOrderItem = orderItems.get(0);

        int totalQuantity = orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        BigDecimal totalPrice = orderItems.stream()
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
