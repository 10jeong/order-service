package com.yeoljeong.tripmate.order.infrastructure.external;

import com.yeoljeong.tripmate.order.infrastructure.external.dto.PlanParticipationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "plan-service", path = "/internal/plan-units")
public interface PlanFeignClient {
    @GetMapping("/{planUnitId}/participations/users/{userId}")
    PlanParticipationResponse getPlanParticipation(@RequestHeader("X-User-Id") String headerUserId, @RequestHeader("X-User-Role") String headerUserRole, @PathVariable("planUnitId") UUID planUnitId, @PathVariable("userId") UUID userId);
}
