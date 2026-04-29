package com.yeoljeong.tripmate.order.domain.repository;

import com.yeoljeong.tripmate.order.domain.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    // 단위 일정에 귀속된 상품을 사용자가 이미 주문한 이력이 있는지
    boolean existsByUserIdAndPlanUnitId(UUID userId, UUID planUnitId);

    // 사용자의 주문 단건 조회
    Optional<Order> findByIdAndUserId(UUID orderId, UUID userId);

    // 사용자의 주문 목록 조회
    Slice<Order> findAllByUserId(UUID userId, Pageable pageable);

    Order save(Order order);
}
