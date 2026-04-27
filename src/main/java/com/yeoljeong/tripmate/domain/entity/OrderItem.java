package com.yeoljeong.tripmate.domain.entity;

import com.yeoljeong.tripmate.domain.BaseAuditEntity;
import com.yeoljeong.tripmate.domain.exception.OrderErrorCode;
import com.yeoljeong.tripmate.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/*
    순수 도메인 요구사항 (OrderItem)
    - 하나의 주문은 하나 이상의 주문 항목을 가질 수 있지만, 현재 정책상 하나의 주문은 하나의 주문 항목만 담을 수 있다.
    - 주문 항목은 하나의 주문에 종속된다.
    - 주문 항목은 하나의 단위 일정(plan_unit_id)에 종속된다.
    - 상품 정보는 상품 ID, 상품명, 업체명, 주소(국가 코드, 주, 시), 단가, 스케줄 정보를 스냅샷으로 저장한다.
    - 주문 시점의 상품 단가는 0 이상이어야 한다.
    - 주문은 구매할 상품의 수량 정보를 가진다.
    - 주문 수량은 1개 이상이어야 한다.
    - 주문 항목은 상품을 사용할 체험 예정일 정보를 가진다.
    - 체험 예정일은 현재 날짜 이후 또는 당일이어야 한다.
 */

@Getter
@Entity
@Table(name = "p_order_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseAuditEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "plan_unit_id", nullable = false)
    private UUID planUnitId;

    @Embedded
    private ProductInfo productInfo;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "experience_date", nullable = false)
    private LocalDate experienceDate;

    @Builder
    private OrderItem(Order order, UUID planUnitId, UUID productId, String productName, BigDecimal price, String companyName,
                      String country, String state, String city, UUID scheduleId, int quantity, LocalDate experienceDate) {
        this.order = order;
        this.planUnitId = planUnitId;
        this.productInfo = ProductInfo.of(productId, productName, price, companyName, country, state, city, scheduleId);
        this.quantity = quantity;
        this.experienceDate = experienceDate;
    }

    static OrderItem create(Order order, UUID planUnitId, UUID productId, String productName, BigDecimal price, String companyName,
                            String country, String state, String city, UUID scheduleId, int quantity, LocalDate experienceDate, LocalDate today) {
        validateOrder(order);
        validatePrice(price);
        validateQuantity(quantity);
        validateExperienceDate(experienceDate, today);
        validateRequiredIds(planUnitId, productId, scheduleId);
        validateRequiredTexts(productName, companyName, country, state, city);

        return OrderItem.builder()
                .order(order)
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
                .build();
    }

    public void delete(UUID userId) {
        super.softDelete();
    }

    private static void validateOrder(Order order) {
        if (order == null) {
            throw new BusinessException(OrderErrorCode.INVALID_ORDER);
        }
    }

    private static void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(OrderErrorCode.INVALID_PRICE);
        }
    }

    private static void validateQuantity(int quantity) {
        if (quantity < 1) {
            throw new BusinessException(OrderErrorCode.INVALID_QUANTITY);
        }
    }

    private static void validateExperienceDate(LocalDate experienceDate, LocalDate today) {
        if (today == null || experienceDate == null || experienceDate.isBefore(today)) {
            throw new BusinessException(OrderErrorCode.INVALID_EXPERIENCE_DATE);
        }
    }

    private static void validateRequiredIds(UUID planUnitId, UUID productId, UUID scheduleId) {
        if (planUnitId == null || productId == null || scheduleId == null) {
            throw new BusinessException(OrderErrorCode.INVALID_ID_FIELD);
        }
    }

    private static void validateRequiredTexts(String productName, String companyName, String country, String state, String city) {
        if (isBlank(productName) || isBlank(companyName) || isBlank(country) || isBlank(state) || isBlank(city)) {
            throw new BusinessException(OrderErrorCode.INVALID_TEXT_FIELD);
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}