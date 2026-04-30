package com.yeoljeong.tripmate.order.application.dto.command;

import java.util.UUID;

public record ApprovalUserCommand(
        UUID planUnitId,
        UUID userId,
        String status
) {}
