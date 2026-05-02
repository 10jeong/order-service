package com.yeoljeong.tripmate.order.infrastructure.messaging.event;

import java.util.UUID;

public record PaymentCompletedEvent(
        UUID eventId,
        UUID orderId,
        UUID paymentId
) { }
