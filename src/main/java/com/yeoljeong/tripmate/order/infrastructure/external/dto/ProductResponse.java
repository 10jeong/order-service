package com.yeoljeong.tripmate.order.infrastructure.external.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ProductResponse(
        UUID productId,
        String productName,
        String country,
        String state,
        String city,
        BigDecimal price,
        String productStatus,
        UUID scheduleId,
        LocalDate date,
        Integer stock,
        String scheduleStatus
) {}
