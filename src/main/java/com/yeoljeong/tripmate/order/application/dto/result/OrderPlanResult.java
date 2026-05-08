package com.yeoljeong.tripmate.order.application.dto.result;

import java.util.UUID;

public record OrderPlanResult(
        UUID orderId,
        UUID planUnitId
) { }
