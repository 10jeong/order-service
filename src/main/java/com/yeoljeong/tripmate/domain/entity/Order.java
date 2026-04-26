package com.yeoljeong.tripmate.domain.entity;

import com.yeoljeong.tripmate.domain.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

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
}
