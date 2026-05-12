package com.yeoljeong.tripmate.order.infrastructure.persistence.jpa;

import com.yeoljeong.tripmate.order.domain.enums.OrderStatus;
import com.yeoljeong.tripmate.order.domain.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {
    boolean existsByUserIdAndOrderItems_PlanUnitIdAndOrderStatusIn(UUID userId, UUID planUnitId, Collection<OrderStatus> orderStatuses);
    Optional<Order> findByIdAndUserId(UUID orderId, UUID userId);
    Slice<Order> findAllByUserId(UUID userId, Pageable pageable);
    Optional<Order> findByUserIdAndOrderItems_PlanUnitId(UUID userId, UUID planUnitId);
    boolean existsByUserIdAndOrderStatus(UUID userId, OrderStatus orderStatus);
    Slice<Order> findAllByOrderStatusAndCreatedAtBefore(OrderStatus orderStatus, LocalDateTime timeoutThreshold, Pageable pageable);
}
