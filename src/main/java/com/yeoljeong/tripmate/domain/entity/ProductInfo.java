package com.yeoljeong.tripmate.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductInfo {
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String city;

    @Column(name = "schedule_id", nullable = false)
    private UUID scheduleId;

    protected ProductInfo(UUID productId, String productName, BigDecimal price, String companyName,
                          String country, String state, String city, UUID scheduleId) {
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
                                 String country, String state, String city, UUID scheduleId) {
        return new ProductInfo(productId, productName, price, companyName, country, state, city, scheduleId);
    }
}
