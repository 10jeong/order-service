package com.yeoljeong.tripmate.order.infrastructure.external.dto;

import java.util.UUID;

public record PlanParticipationResponse(
        UUID planUnitId,
        UUID userId,
        String status
) {}
