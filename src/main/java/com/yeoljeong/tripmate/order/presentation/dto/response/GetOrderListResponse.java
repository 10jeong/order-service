package com.yeoljeong.tripmate.order.presentation.dto.response;

import com.yeoljeong.tripmate.order.application.dto.result.GetOrderListResult;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record GetOrderListResponse(
        UUID orderId,
        UUID userId,
        String orderStatus,
        String productName,
        Integer totalQuantity,
        BigDecimal totalPrice,
        LocalDate experienceDate
) {
    public static GetOrderListResponse from(GetOrderListResult result) {
        return GetOrderListResponse.builder()
                .orderId(result.orderId())
                .userId(result.userId())
                .orderStatus(result.orderStatus())
                .productName(result.productName())
                .totalQuantity(result.totalQuantity())
                .totalPrice(result.totalPrice())
                .experienceDate(result.experienceDate())
                .build();
    }
}
