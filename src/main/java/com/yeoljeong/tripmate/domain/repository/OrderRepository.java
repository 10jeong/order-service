package com.yeoljeong.tripmate.domain.repository;

import com.yeoljeong.tripmate.domain.entity.Order;

import java.util.UUID;

public interface OrderRepository {

    // 단위 일정에 귀속된 상품을 사용자가 이미 주문한 이력이 있는지
    boolean existsByUserIdAndPlanUnitId(UUID userId, UUID planUnitId);

    Order save(Order order);
}
