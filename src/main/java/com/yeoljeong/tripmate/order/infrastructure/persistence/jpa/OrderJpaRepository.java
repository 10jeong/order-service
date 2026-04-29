package com.yeoljeong.tripmate.order.infrastructure.persistence.jpa;

import com.yeoljeong.tripmate.order.domain.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {
    boolean existsByUserIdAndOrderItems_PlanUnitId(UUID userId, UUID planUnitId);
    Optional<Order> findByIdAndUserId(UUID orderId, UUID userId);
    Slice<Order> findAllByUserId(UUID userId, Pageable pageable);
}
