package com.yeoljeong.tripmate.order.infrastructure.outbox;

import com.yeoljeong.tripmate.domain.constants.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderOutboxDispatcher {

    private final OrderOutboxRepository orderOutboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Transactional
    @Scheduled(fixedDelay = 1000)
    public void dispatch() {
        List<OrderOutbox> pendingEvents = orderOutboxRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        pendingEvents.forEach(outbox -> {
            try {
                kafkaTemplate.send(outbox.getTopic(), outbox.getPayload()).get(5, TimeUnit.SECONDS);
                outbox.published();
            } catch (InterruptedException e) {
                // 예외가 발생하며 유실된 인터럽트 상태 복구
                Thread.currentThread().interrupt();
                log.error("OrderOutbox 발행 인터럽트 - outboxId={}, topic={}, retryCount={}",
                        outbox.getId(), outbox.getTopic(), outbox.getRetryCount(), e);

                outbox.fail();
            } catch (ExecutionException | TimeoutException e) {
                log.error("OrderOutbox 발행 실패 - outboxId={}, topic={}, retryCount={}",
                        outbox.getId(), outbox.getTopic(), outbox.getRetryCount(), e);

                outbox.fail();
            }
        });
    }
}
