package com.yeoljeong.tripmate.application.dto.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
        UUID userId,
        List<OrderItemCommand> orderItems
) {
    public record OrderItemCommand(
            UUID planUnitId,
            UUID productId,
            String productName,
            BigDecimal price,
            String companyName,
            String country,
            String state,
            String city,
            UUID scheduleId,
            Integer quantity,
            LocalDate experienceDate
    ) {
    }
}
