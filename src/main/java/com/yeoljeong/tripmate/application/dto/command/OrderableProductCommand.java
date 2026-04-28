package com.yeoljeong.tripmate.application.dto.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public record OrderableProductCommand(
        UUID productId,
        String productName,
        String companyName,
        String country,
        String state,
        String city,
        BigDecimal price,
        String productStatus,
        UUID productScheduleId,
        LocalDate date,
        Integer stock,
        String scheduleStatus
) {}
