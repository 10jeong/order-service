package com.yeoljeong.tripmate.order.infrastructure.messaging.event;

import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        UUID planUnitId,
        UUID productId,
        UUID scheduleId,
        int quantity
) { }
