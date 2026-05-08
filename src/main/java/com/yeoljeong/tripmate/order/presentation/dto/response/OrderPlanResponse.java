package com.yeoljeong.tripmate.order.presentation.dto.response;

import java.util.UUID;

public record OrderPlanResponse(
        UUID orderId,
        UUID planUnitId
) { }
