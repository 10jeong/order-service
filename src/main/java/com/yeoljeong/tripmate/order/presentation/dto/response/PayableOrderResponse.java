package com.yeoljeong.tripmate.order.presentation.dto.response;

import com.yeoljeong.tripmate.order.application.dto.result.PayableOrderResult;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record PayableOrderResponse(
        UUID orderId,
        UUID userId,
        String orderName,
        BigDecimal amount,
        String orderStatus
) {
    public static PayableOrderResponse from(PayableOrderResult result) {
        return PayableOrderResponse.builder()
                .orderId(result.orderId())
                .userId(result.userId())
                .orderName(result.orderName())
                .amount(result.amount())
                .orderStatus(result.orderStatus())
                .build();
    }
}
