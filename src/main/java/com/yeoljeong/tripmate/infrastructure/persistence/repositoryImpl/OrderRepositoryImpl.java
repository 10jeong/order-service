package com.yeoljeong.tripmate.infrastructure.persistence.repositoryImpl;

import com.yeoljeong.tripmate.domain.entity.Order;
import com.yeoljeong.tripmate.domain.repository.OrderRepository;
import com.yeoljeong.tripmate.infrastructure.persistence.jpa.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }
}
