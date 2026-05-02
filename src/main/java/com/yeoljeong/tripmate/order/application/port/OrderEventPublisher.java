package com.yeoljeong.tripmate.order.application.port;

import com.yeoljeong.tripmate.event.OrderCreatedEvent;

public interface OrderEventPublisher {
    void publishOrderCreated(OrderCreatedEvent event);
}
