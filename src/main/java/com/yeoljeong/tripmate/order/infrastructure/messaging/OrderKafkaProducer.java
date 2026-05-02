package com.yeoljeong.tripmate.order.infrastructure.messaging;

import com.yeoljeong.tripmate.event.enums.OrderTopic;
import com.yeoljeong.tripmate.event.OrderCreatedEvent;
import com.yeoljeong.tripmate.order.application.port.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaProducer implements OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishOrderCreated(OrderCreatedEvent event) {
        kafkaTemplate.send(OrderTopic.ORDER_CREATED_TOPIC, event.planUnitId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("order.created 발행 실패 - eventId={}, planUnitId={}, productId={}, scheduleId={}",
                                event.eventId(), event.planUnitId(), event.productId(), event.scheduleId(), ex);
                    }
                });
    }
}
