package com.yeoljeong.tripmate.order.infrastructure.outbox;

import com.yeoljeong.tripmate.domain.Outbox;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Table(name = "order_outbox")
public class OrderOutbox extends Outbox {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    public static OrderOutbox create(String topic, String payload) {
        OrderOutbox outbox = new OrderOutbox();
        Outbox.init(outbox, topic, payload);
        return outbox;
    }
}
