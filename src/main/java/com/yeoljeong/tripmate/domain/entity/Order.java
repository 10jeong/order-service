package com.yeoljeong.tripmate.domain.entity;

import com.yeoljeong.tripmate.domain.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

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
    private Date experienceDate;

    @Column(name = "cancelled_at")
    private Timestamp cancelledAt;

    @Column(name = "cancel_reason")
    private String cancelReason;


    @Builder
    public Order(UUID userId, OrderStatus orderStatus, UUID planUnitId, UUID productId, String productName, BigDecimal price, String companyName,
                 String country, String state, String city, UUID scheduleId, int quantity, Date experienceDate, Timestamp cancelledAt, String cancelReason) {
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
                        String country, String state, String city, UUID scheduleId, int quantity, Date experienceDate) {
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
}
