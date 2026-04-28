package com.yeoljeong.tripmate.order.application.dto.command;

import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.order.domain.exception.OrderErrorCode;

import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
        UUID userId,
        List<OrderItemCommand> orderItems
) {
    public CreateOrderCommand {
        if (userId == null) {
            throw new BusinessException(OrderErrorCode.REQUIRED_USER_ID);
        }

        if (orderItems == null || orderItems.size() != 1 || orderItems.get(0) == null) {
            throw new BusinessException(OrderErrorCode.INVALID_ORDER_ITEM_COUNT);
        }
    }

    public record OrderItemCommand(
            UUID planUnitId,
            UUID productId,
            UUID scheduleId,
            Integer quantity
    ) {
        public OrderItemCommand {
            if (planUnitId == null || productId == null || scheduleId == null) {
                throw new BusinessException(OrderErrorCode.REQUIRED_ORDER_ITEM_INFO);
            }

            if (quantity == null || quantity < 1) {
                throw new BusinessException(OrderErrorCode.INVALID_ORDER_QUANTITY);
            }
        }
    }
}
