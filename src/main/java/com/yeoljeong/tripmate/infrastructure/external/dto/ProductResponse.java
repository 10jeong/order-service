package com.yeoljeong.tripmate.infrastructure.external.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public record ProductResponse(
        UUID productId,
        String productName,
        String companyName,
        String country,
        String state,
        String city,
        BigDecimal price,
        String productStatus,
        UUID productScheduleId,
        Date date,
        int stock,
        String scheduleStatus
) {}
