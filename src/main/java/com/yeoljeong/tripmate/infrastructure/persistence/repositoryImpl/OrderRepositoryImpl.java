package com.yeoljeong.tripmate.infrastructure.persistence.repositoryImpl;

import com.yeoljeong.tripmate.domain.entity.Order;
import com.yeoljeong.tripmate.domain.repository.OrderRepository;
import com.yeoljeong.tripmate.infrastructure.persistence.jpa.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }
}
