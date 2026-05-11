package com.yeoljeong.tripmate.order.infrastructure.external;

import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.order.application.client.PlanClient;
import com.yeoljeong.tripmate.order.application.dto.command.ApprovalUserCommand;
import com.yeoljeong.tripmate.order.domain.exception.OrderErrorCode;
import com.yeoljeong.tripmate.order.infrastructure.external.dto.PlanParticipationResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlanAdapter implements PlanClient {

    private final PlanFeignClient planFeignClient;

    @Override
    public ApprovalUserCommand getPlanParticipation(UUID userId, UUID planUnitId) {
        try {
            PlanParticipationResponse planParticipationResponse = planFeignClient.getPlanParticipation(planUnitId, userId);

            if (planParticipationResponse == null) {
                throw new BusinessException(OrderErrorCode.PLAN_PARTICIPATION_NOT_FOUND);
            }

            return new ApprovalUserCommand(planParticipationResponse.planUnitId(), planParticipationResponse.userId(), planParticipationResponse.status());
        } catch (FeignException.NotFound e) {
            throw new BusinessException(OrderErrorCode.PLAN_PARTICIPATION_NOT_FOUND);

        } catch (FeignException e) {
            throw new BusinessException(OrderErrorCode.PLAN_SERVICE_ERROR);
        }
    }
}
