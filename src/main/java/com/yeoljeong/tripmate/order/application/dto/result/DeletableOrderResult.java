package com.yeoljeong.tripmate.order.application.dto.result;

public record DeletableOrderResult(
        boolean exists,
        String paymentStatus
) { }
