package com.yeoljeong.tripmate.order.infrastructure.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.order.application.port.OrderOutboxRecorder;
import com.yeoljeong.tripmate.order.domain.exception.OrderErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderOutboxRecorderImpl implements OrderOutboxRecorder {

    private final OrderOutboxRepository orderOutboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void record(String topic, Object event) {

        if (topic == null || topic.isBlank()) {
            throw new BusinessException(OrderErrorCode.INVALID_TOPIC);
        }
        if (event == null) {
            throw new BusinessException(OrderErrorCode.INVALID_EVENT);
        }

        try {
            String payload = objectMapper.writeValueAsString(event);

            OrderOutbox outboxEvent = OrderOutbox.create(topic, payload);

            orderOutboxRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new BusinessException(OrderErrorCode.OUTBOX_SERIALIZATION_FAILED);
        }
    }
}
