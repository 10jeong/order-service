package com.yeoljeong.tripmate.order.application.client;

import com.yeoljeong.tripmate.order.application.dto.command.ApprovalUserCommand;

import java.util.UUID;

public interface PlanClient {
    ApprovalUserCommand getPlanParticipation(UUID userId, UUID planUnitId);
}
