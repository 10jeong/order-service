package com.yeoljeong.tripmate.order.infrastructure.persistence.repositoryImpl;

import com.yeoljeong.tripmate.order.domain.enums.OrderStatus;
import com.yeoljeong.tripmate.order.domain.model.Order;
import com.yeoljeong.tripmate.order.domain.repository.OrderRepository;
import com.yeoljeong.tripmate.order.infrastructure.persistence.jpa.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public boolean existsByUserIdAndPlanUnitIdAndOrderStatusIn(UUID userId, UUID planUnitId, Collection<OrderStatus> orderStatuses) {
        return orderJpaRepository.existsByUserIdAndOrderItems_PlanUnitIdAndOrderStatusIn(userId, planUnitId, orderStatuses);
    }

    @Override
    public Optional<Order> findByIdAndUserId(UUID orderId, UUID userId) {
        return orderJpaRepository.findByIdAndUserId(orderId, userId);
    }

    @Override
    public Slice<Order> findAllByUserId(UUID userId, Pageable pageable) {
        return orderJpaRepository.findAllByUserId(userId, pageable);
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return orderJpaRepository.findById(orderId);
    }

    @Override
    public Optional<Order> findByUserIdAndPlanUnitId(UUID userId, UUID planUnitId) {
        return orderJpaRepository.findByUserIdAndOrderItems_PlanUnitId(userId, planUnitId);
    }

    @Override
    public boolean existsByUserIdAndOrderStatus(UUID userId, OrderStatus orderStatus) {
        return orderJpaRepository.existsByUserIdAndOrderStatus(userId, orderStatus);
    }

    @Override
    public Slice<Order> findAllByOrderStatusAndCreatedAtBefore(OrderStatus orderStatus, LocalDateTime timeoutThreshold, Pageable pageable) {
        return orderJpaRepository.findAllByOrderStatusAndCreatedAtBefore(orderStatus, timeoutThreshold, pageable);
    }

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }
}
