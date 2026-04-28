package com.yeoljeong.tripmate.application.dto.command;

import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
        UUID userId,
        List<OrderItemCommand> orderItems
) {
    public record OrderItemCommand(
            UUID planUnitId,
            UUID productId,
            UUID scheduleId,
            Integer quantity
    ) {
    }
}
