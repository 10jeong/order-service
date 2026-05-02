package com.yeoljeong.tripmate.order.infrastructure.messaging;

import com.yeoljeong.tripmate.event.enums.PaymentTopic;
import com.yeoljeong.tripmate.order.application.service.command.OrderCommandService;
import com.yeoljeong.tripmate.order.application.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderKafkaConsumer {

    private final OrderCommandService commandService;

    @KafkaListener(topics = PaymentTopic.PAYMENT_COMPLETED_TOPIC)
    public void consumePaymentCompleted(PaymentCompletedEvent event, Acknowledgment acknowledgment) {
        commandService.completePayment(event.orderId());
        acknowledgment.acknowledge();
    }
}
