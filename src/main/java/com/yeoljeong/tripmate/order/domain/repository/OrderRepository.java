package com.yeoljeong.tripmate.order.domain.repository;

import com.yeoljeong.tripmate.order.domain.enums.OrderStatus;
import com.yeoljeong.tripmate.order.domain.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    // 단위 일정에 귀속된 상품을 사용자가 이미 주문한 이력이 있는지
    boolean existsByUserIdAndPlanUnitId(UUID userId, UUID planUnitId);

    // 사용자의 주문 단건 조회
    Optional<Order> findByIdAndUserId(UUID orderId, UUID userId);

    // 사용자의 주문 목록 조회
    Slice<Order> findAllByUserId(UUID userId, Pageable pageable);

    // 주문 단건 조회
    Optional<Order> findById(UUID orderId);

    // userId와 planUnitId로 주문 단건 조회
    Optional<Order> findByUserIdAndPlanUnitId(UUID userId, UUID planUnitId);

    // userId와 orderStatus로 주문 조회
    boolean existsByUserIdAndOrderStatus(UUID userId, OrderStatus orderStatus);

    // orderStatus와 createdAt 기반 임계값(15분)으로 주문 리스트 조회
    Slice<Order> findAllByOrderStatusAndCreatedAtBefore(OrderStatus orderStatus, LocalDateTime timeoutThreshold, Pageable pageable);

    Order save(Order order);
}
