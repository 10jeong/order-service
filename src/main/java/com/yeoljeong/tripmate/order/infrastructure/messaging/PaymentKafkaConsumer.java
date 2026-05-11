package com.yeoljeong.tripmate.order.infrastructure.messaging;

import com.yeoljeong.tripmate.event.PaymentCompletedEvent;
import com.yeoljeong.tripmate.event.enums.PaymentTopic;
import com.yeoljeong.tripmate.order.application.service.command.OrderCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaConsumer {

    private final OrderCommandService commandService;
    private final KafkaPayloadDeserializer payloadDeserializer;

    @KafkaListener(
            topics = PaymentTopic.PAYMENT_COMPLETED_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePaymentCompleted(String payload, Acknowledgment acknowledgment) {

        try {
            PaymentCompletedEvent event = payloadDeserializer.deserialize(payload, PaymentCompletedEvent.class);

            log.info("[Order] payment.completed 이벤트 수신: orderId={}", event.orderId());

            commandService.completePayment(event.orderId());
            acknowledgment.acknowledge();

            log.info("[Order] payment.completed 이벤트 처리 성공: orderId={}", event.orderId());
        } catch (Exception e) {
            log.error("[Order] payment.completed 이벤트 처리 실패, 재시도 예정: payload={}, error={}",
                    payload, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
