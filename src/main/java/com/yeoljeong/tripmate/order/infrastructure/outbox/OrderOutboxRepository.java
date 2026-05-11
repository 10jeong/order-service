package com.yeoljeong.tripmate.order.infrastructure.outbox;

import com.yeoljeong.tripmate.domain.constants.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderOutboxRepository extends JpaRepository<OrderOutbox, UUID> {
    List<OrderOutbox> findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
