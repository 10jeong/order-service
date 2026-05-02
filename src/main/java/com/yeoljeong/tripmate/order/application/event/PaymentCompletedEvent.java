package com.yeoljeong.tripmate.order.application.event;

import java.util.UUID;

public record PaymentCompletedEvent(
        UUID eventId,
        UUID orderId,
        UUID paymentId
) { }
