package com.yeoljeong.tripmate.order.infrastructure.outbox;

import com.yeoljeong.tripmate.domain.constants.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderOutboxDispatcher {

    private final OrderOutboxRepository orderOutboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 1000)
    public void dispatch() {
        List<OrderOutbox> pendingEvents = orderOutboxRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        pendingEvents.forEach(this::dispatchOne);
    }

    @Transactional
    protected void dispatchOne(OrderOutbox outbox) {
        try {
            kafkaTemplate.send(outbox.getTopic(), outbox.getPayload()).get();
            outbox.published();
        } catch (Exception e) {
            log.error("OrderOutbox 발행 실패 - outboxId={}, topic={}, retryCount={}",
                    outbox.getId(), outbox.getTopic(), outbox.getRetryCount(), e);

            outbox.fail();
        }
    }
}
