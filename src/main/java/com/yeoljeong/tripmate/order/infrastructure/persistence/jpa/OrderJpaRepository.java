package com.yeoljeong.tripmate.order.infrastructure.persistence.jpa;

import com.yeoljeong.tripmate.order.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {
    boolean existsByUserIdAndOrderItems_PlanUnitId(UUID userId, UUID planUnitId);
}
