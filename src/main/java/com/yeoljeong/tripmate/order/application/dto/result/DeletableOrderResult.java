package com.yeoljeong.tripmate.order.application.dto.result;

import java.util.UUID;

public record DeletableOrderResult(
        boolean exists,
        String paymentStatus
) { }
