package com.yeoljeong.tripmate.domain.entity;

import com.yeoljeong.tripmate.domain.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/*
    순수 도메인 요구사항
    - 하나의 주문은 하나의 상품만 담을 수 있다.
    - 주문은 하나의 단위 일정(plan_unit_id)에 종속된다.
    - 상품 정보는 상품 ID, 상품명, 업체명, 주소(국가 코드, 주, 시), 단가, 스케줄 정보를 스냅샷으로 저장한다.
    - 주문 시점의 상품 단가는 0 이상이어야 한다.
    - 주문은 구매할 상품의 수량 정보를 가진다.
    - 주문 수량은 1개 이상이어야 한다.
    - 주문은 상품을 사용할 체험 예정일 정보를 가진다.
    - 체험 예정일은 현재 날짜 이후 또는 당일이어야 한다.
    - 주문 생성 시 CONFIRMED 상태의 주문을 생성한다.
    - CONFIRMED 상태의 주문은 완료 처리 시 COMPLETED 상태로 변경된다.
    - CONFIRMED 또는 COMPLETED 상태의 주문은 취소 처리 시 CANCELLED 상태로 변경된다.
    - 주문 취소 시 주문 취소 시각과 취소 사유를 저장한다.
    - 주문 상태가 CANCELLED인 경우 cancelled_at과 cancel_reason은 반드시 존재해야 한다.
    - 주문 상태가 CANCELLED가 아닌 경우 cancelled_at과 cancel_reason은 null이어야 한다.
 */

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", length = 20, nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "plan_unit_id", nullable = false)
    private UUID planUnitId;

    @Embedded
    private ProductInfo productInfo;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "experience_date", nullable = false)
    private LocalDate experienceDate;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancel_reason")
    private String cancelReason;


    @Builder
    private Order(UUID userId, OrderStatus orderStatus, UUID planUnitId, UUID productId, String productName, BigDecimal price, String companyName,
                 String country, String state, String city, UUID scheduleId, int quantity, LocalDate experienceDate, LocalDateTime cancelledAt, String cancelReason) {
        this.userId = userId;
        this.orderStatus = orderStatus;
        this.planUnitId = planUnitId;
        this.productInfo = ProductInfo.of(productId, productName, price, companyName, country, state, city, scheduleId);
        this.quantity = quantity;
        this.experienceDate = experienceDate;
        this.cancelledAt = cancelledAt;
        this.cancelReason = cancelReason;
    }

    public static Order create(UUID userId, UUID planUnitId, UUID productId, String productName, BigDecimal price, String companyName,
                        String country, String state, String city, UUID scheduleId, int quantity, LocalDate experienceDate, LocalDate today) {
        validateQuantity(quantity);
        validatePrice(price);
        validateExperienceDate(experienceDate, today);

        return Order.builder()
                .userId(userId)
                .orderStatus(OrderStatus.CONFIRMED)
                .planUnitId(planUnitId)
                .productId(productId)
                .productName(productName)
                .price(price)
                .companyName(companyName)
                .country(country)
                .state(state)
                .city(city)
                .scheduleId(scheduleId)
                .quantity(quantity)
                .experienceDate(experienceDate)
                .cancelledAt(null)
                .cancelReason(null)
                .build();
    }

    // CONFIRMED -> COMPLETED
    public void complete() {
        if (this.orderStatus != OrderStatus.CONFIRMED) {
            // throw INVALID_ORDER_STATUS
        }

        this.orderStatus = OrderStatus.COMPLETED;
    }

    private static void validateQuantity(int quantity) {
        if (quantity < 1) {
            // throw INVALID_QUANTITY
        }
    }

    private static void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            // throw INVALID_PRICE
        }
    }

    private static void validateExperienceDate(LocalDate experienceDate, LocalDate today) {
        if (experienceDate == null || experienceDate.isBefore(today)) {
            // throw INVALID_EXPERIENCE_DATE
        }
    }
}
