package com.yeoljeong.tripmate.order.infrastructure.messaging;

import com.yeoljeong.tripmate.event.PlanUnitAddParticipantFailedEvent;
import com.yeoljeong.tripmate.event.PlanUnitDeductParticipantEvent;
import com.yeoljeong.tripmate.event.PlanUnitParticipantQuitEvent;
import com.yeoljeong.tripmate.event.enums.PlanTopic;
import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.order.application.service.command.OrderCommandService;
import com.yeoljeong.tripmate.order.domain.enums.OrderCancelReason;
import com.yeoljeong.tripmate.order.domain.exception.OrderErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlanKafkaConsumer {

    private final OrderCommandService commandService;

    @KafkaListener(
            topics = PlanTopic.PLAN_UNIT_PARTICIPANT_QUIT_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePlanUnitParticipantQuit(PlanUnitParticipantQuitEvent event, Acknowledgment acknowledgment) {
        log.info("[Order] plan.unit.participant.quit 이벤트 수신: planUnitId={}", event.planUnitId());

        OrderCancelReason reason = (event.reason() == null || event.reason().isBlank())
                ? OrderCancelReason.PLAN_PARTICIPANT_QUIT : OrderCancelReason.from(event.reason());

        try {
            commandService.cancelOrderByParticipantQuit(event.userId(), event.planUnitId(), reason);
            acknowledgment.acknowledge();

            log.info("[Order] plan.unit.participant.quit 이벤트 처리 성공: userId={}, planUnitId={}", event.userId(), event.planUnitId());
        } catch (BusinessException e) {
            if (isNonRetryable(e)) {
                log.warn("[Order] plan.unit.participant.quit 이벤트 처리 스킵: userId={}, planUnitId={}", event.userId(), event.planUnitId(), e);

                acknowledgment.acknowledge();
                return;
            }

            log.error("[Order] plan.unit.participant.quit 이벤트 처리 실패, 재시도 예정: userId={}, planUnitId={}, error={}",
                    event.userId(), event.planUnitId(), e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("[Order] plan.unit.participant.quit 이벤트 처리 실패, 재시도 예정: userId={}, planUnitId={}, error={}",
                    event.userId(), event.planUnitId(), e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(
            topics = PlanTopic.PLAN_UNIT_PARTICIPANT_ADD_FAILED_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePlanUnitAddParticipantFailed(PlanUnitAddParticipantFailedEvent event, Acknowledgment acknowledgment) {
        log.info("[Order] plan.unit.participant.add.failed 이벤트 수신: orderId={}", event.orderId());

        handleParticipantDeductOrAddFailed(event.orderId(), OrderCancelReason.PLAN_PARTICIPANT_EXCEEDED, acknowledgment);

        log.info("[Order] plan.unit.participant.add.failed 이벤트 처리 성공: orderId={}", event.orderId());
    }

    @KafkaListener(
            topics = PlanTopic.PLAN_UNIT_PARTICIPANT_DEDUCTED_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePlanUnitDeductParticipant(PlanUnitDeductParticipantEvent event, Acknowledgment acknowledgment) {
        log.info("[Order] plan.unit.participant.deducted 이벤트 수신: orderId={}", event.orderId());

        handleParticipantDeductOrAddFailed(event.orderId(), OrderCancelReason.PRODUCT_STOCK_SHORTAGE, acknowledgment);

        log.info("[Order] plan.unit.participant.deducted 이벤트 처리 성공: orderId={}", event.orderId());
    }

    private void handleParticipantDeductOrAddFailed(UUID orderId, OrderCancelReason reason, Acknowledgment acknowledgment) {
        try {
            commandService.cancelOrderByPlanUnitParticipantRollback(orderId, reason);
            acknowledgment.acknowledge();
        } catch (BusinessException e) {
            if (isNonRetryable(e)) {
                log.warn("[Order] PlanUnit 참여자 보상 트랜잭션 이벤트 처리 스킵: orderId={}, ", orderId, e);

                acknowledgment.acknowledge();
                return;
            }

            log.error("[Order] PlanUnit 참여자 보상 트랜잭션 이벤트 처리 실패, 재시도 예정: orderId={}, error={}",
                    orderId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("[Order] PlanUnit 참여자 보상 트랜잭션 이벤트 처리 실패, 재시도 예정: orderId={}, error={}",
                    orderId, e.getMessage(), e);
            throw e;
        }
    }

    private boolean isNonRetryable(BusinessException e) {
        return e.getErrorCode() == OrderErrorCode.ORDER_NOT_FOUND
                || e.getErrorCode() == OrderErrorCode.INVALID_ORDER_STATUS
                || e.getErrorCode() == OrderErrorCode.INVALID_CANCELLED_AT
                || e.getErrorCode() == OrderErrorCode.INVALID_CANCEL_REASON;
    }
}
