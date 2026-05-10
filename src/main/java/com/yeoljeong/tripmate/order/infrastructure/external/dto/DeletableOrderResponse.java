package com.yeoljeong.tripmate.order.infrastructure.external.dto;

import java.util.UUID;

public record DeletableOrderResponse(
        UUID orderId,
        boolean exists,
        String paymentStatus
) {}
