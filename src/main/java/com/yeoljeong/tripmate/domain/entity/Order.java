package com.yeoljeong.tripmate.domain.entity;

import com.yeoljeong.tripmate.domain.BaseAuditEntity;
import com.yeoljeong.tripmate.domain.enums.Country;
import com.yeoljeong.tripmate.domain.enums.OrderStatus;
import com.yeoljeong.tripmate.domain.exception.OrderErrorCode;
import com.yeoljeong.tripmate.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
    순수 도메인 요구사항 (Order)
    - 주문은 주문자 정보를 가진다.
    - 주문 생성 시 CONFIRMED 상태의 주문을 생성한다.
    - CONFIRMED 상태의 주문은 완료 처리 시 COMPLETED 상태로 변경된다.
    - CONFIRMED 또는 COMPLETED 상태의 주문은 취소 처리 시 CANCELLED 상태로 변경된다.
    - 주문 취소 시 주문 취소 시각과 취소 사유를 저장한다.
    - 주문 상태가 CANCELLED인 경우 cancelled_at과 cancel_reason은 반드시 존재해야 한다.
    - 주문 상태가 CANCELLED가 아닌 경우 cancelled_at과 cancel_reason은 null이어야 한다.
 */

@Entity
@Table(name = "p_order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", length = 30, nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancel_reason", length = 255)
    private String cancelReason;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Builder
    private Order(UUID userId, OrderStatus orderStatus, LocalDateTime cancelledAt, String cancelReason) {
        this.userId = userId;
        this.orderStatus = orderStatus;
        this.cancelledAt = cancelledAt;
        this.cancelReason = cancelReason;
    }

    public static Order create(UUID userId, UUID planUnitId, UUID productId, String productName, BigDecimal price, String companyName,
                               Country country, String state, String city, UUID scheduleId, int quantity, LocalDate experienceDate, LocalDate today) {
        validateRequiredIds(userId);

        Order order = Order.builder()
                .userId(userId)
                .orderStatus(OrderStatus.CONFIRMED)
                .cancelledAt(null)
                .cancelReason(null)
                .build();

        OrderItem orderItem = OrderItem.create(order, planUnitId, productId, productName, price,
                companyName, country, state, city, scheduleId, quantity, experienceDate, today);

        order.addOrderItem(orderItem);

        return order;
    }

    // CONFIRMED -> COMPLETED
    public void complete() {
        if (this.orderStatus != OrderStatus.CONFIRMED) {
            throw new BusinessException(OrderErrorCode.INVALID_ORDER_STATUS);
        }

        this.orderStatus = OrderStatus.COMPLETED;
    }

    // CONFIRMED -> CANCELLED or COMPLETED -> CANCELLED
    public void cancel(LocalDateTime cancelledAt, String cancelReason) {
        if (this.orderStatus != OrderStatus.CONFIRMED && this.orderStatus != OrderStatus.COMPLETED) {
            throw new BusinessException(OrderErrorCode.INVALID_ORDER_STATUS);
        }

        if (cancelledAt == null) {
            throw new BusinessException(OrderErrorCode.INVALID_CANCELLED_AT);
        }

        if (cancelReason == null) {
            throw new BusinessException(OrderErrorCode.INVALID_CANCEL_REASON);
        }

        this.orderStatus = OrderStatus.CANCELLED;
        this.cancelledAt = cancelledAt;
        this.cancelReason = cancelReason;
    }

    public void delete(UUID userId) {
        super.softDelete();
        this.orderItems.forEach(orderItem -> orderItem.delete(userId));
    }

    private void addOrderItem(OrderItem orderItem) {
        if (orderItem == null) {
            throw new BusinessException(OrderErrorCode.INVALID_ORDER_ITEM);
        }

        // 정책에 따라 단건 주문만 허용
        if (!this.orderItems.isEmpty()) {
            throw new BusinessException(OrderErrorCode.INVALID_ORDER_ITEM_COUNT);
        }

        this.orderItems.add(orderItem);
    }

    private static void validateRequiredIds(UUID userId) {
        if (userId == null) {
            throw new BusinessException(OrderErrorCode.INVALID_ID_FIELD);
        }
    }
}
