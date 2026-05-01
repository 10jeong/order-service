package com.yeoljeong.tripmate.order.application.service.query;

import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.order.application.dto.result.OrderResult;
import com.yeoljeong.tripmate.order.application.dto.result.GetOrderListResult;
import com.yeoljeong.tripmate.order.application.dto.result.PayableOrderResult;
import com.yeoljeong.tripmate.order.domain.exception.OrderErrorCode;
import com.yeoljeong.tripmate.order.domain.model.Order;
import com.yeoljeong.tripmate.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

    private final OrderRepository orderRepository;

    // 주문 단건 조회
    public OrderResult getOrder(UUID orderId, UUID userId) {

        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));

        return OrderResult.from(order);
    }

    // 주문 목록 조회
    public Slice<GetOrderListResult> getOrders(UUID userId, Pageable pageable) {

        Slice<Order> orders = orderRepository.findAllByUserId(userId, pageable);

        return orders.map(GetOrderListResult::from);
    }

    // 결제 가능 주문 조회
    public PayableOrderResult getPayableOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));

        return PayableOrderResult.from(order);
    }
}
