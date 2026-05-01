package com.yeoljeong.tripmate.order.application.dto.result;

import com.yeoljeong.tripmate.order.domain.model.Order;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record PayableOrderResult(
        UUID orderId,
        UUID userId,
        String orderName,
        BigDecimal amount,
        String orderStatus
) {
    public static PayableOrderResult from(Order order) {
        return PayableOrderResult.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .orderName(order.getOrderItems().get(0).getProductInfo().getProductName())
                .amount(order.getOrderItems().get(0).getProductInfo().getPrice())
                .orderStatus(order.getOrderStatus().toString())
                .build();
    }
}
