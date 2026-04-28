package com.yeoljeong.tripmate.order.presentation.dto.request;

import com.yeoljeong.tripmate.order.application.dto.command.CreateOrderCommand;

import java.util.List;
import java.util.UUID;

public record OrderRequest(
        UUID userId,
        List<OrderItemRequest> orderItems
) {
    public CreateOrderCommand toCommand() {
        return new CreateOrderCommand(
                userId,
                orderItems.stream()
                        .map(OrderItemRequest::toCommand)
                        .toList()
        );
    }

    public record OrderItemRequest(
            UUID planUnitId,
            UUID productId,
            UUID scheduleId,
            Integer quantity
    ) {
        public CreateOrderCommand.OrderItemCommand toCommand() {
            return new CreateOrderCommand.OrderItemCommand(
                    planUnitId,
                    productId,
                    scheduleId,
                    quantity
            );
        }
    }
}
