package com.yeoljeong.tripmate.order.infrastructure.messaging.handler;

import com.yeoljeong.tripmate.event.OrderCreatedEvent;
import com.yeoljeong.tripmate.order.application.port.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderCreatedEventHandler {

    private final OrderEventPublisher orderEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderCreatedEvent event) {
        orderEventPublisher.publishOrderCreated(event);
    }
}
