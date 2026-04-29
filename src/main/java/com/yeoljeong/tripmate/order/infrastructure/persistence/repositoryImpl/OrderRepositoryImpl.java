package com.yeoljeong.tripmate.order.infrastructure.persistence.repositoryImpl;

import com.yeoljeong.tripmate.order.domain.model.Order;
import com.yeoljeong.tripmate.order.domain.repository.OrderRepository;
import com.yeoljeong.tripmate.order.infrastructure.persistence.jpa.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public boolean existsByUserIdAndPlanUnitId(UUID userId, UUID planUnitId) {
        return orderJpaRepository.existsByUserIdAndOrderItems_PlanUnitId(userId, planUnitId);
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
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }
}
