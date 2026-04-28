package com.yeoljeong.tripmate.order.domain.model;

import com.yeoljeong.tripmate.order.domain.enums.Country;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductInfo {
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_name", length = 255, nullable = false)
    private String productName;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "company_name", length = 100, nullable = false)
    private String companyName;

    @Enumerated(EnumType.STRING)
    @Column(length = 2, nullable = false)
    private Country country;

    @Column(length = 255, nullable = false)
    private String state;

    @Column(length = 255, nullable = false)
    private String city;

    @Column(name = "schedule_id", nullable = false)
    private UUID scheduleId;

    protected ProductInfo(UUID productId, String productName, BigDecimal price, String companyName,
                          Country country, String state, String city, UUID scheduleId) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.companyName = companyName;
        this.country = country;
        this.state = state;
        this.city = city;
        this.scheduleId = scheduleId;
    }

    public static ProductInfo of(UUID productId, String productName, BigDecimal price, String companyName,
                                 Country country, String state, String city, UUID scheduleId) {
        return new ProductInfo(productId, productName, price, companyName, country, state, city, scheduleId);
    }
}
