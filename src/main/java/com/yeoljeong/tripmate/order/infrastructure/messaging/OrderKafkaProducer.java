package com.yeoljeong.tripmate.order.infrastructure.messaging;

import com.yeoljeong.tripmate.event.enums.OrderTopic;
import com.yeoljeong.tripmate.order.infrastructure.messaging.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderCreated(OrderCreatedEvent event) {
        kafkaTemplate.send(OrderTopic.ORDER_CREATED_TOPIC, event.planUnitId().toString(), event);
    }
}
