package com.yeoljeong.tripmate.order.infrastructure.external;

import com.yeoljeong.tripmate.order.infrastructure.external.dto.ProductResponse;
import com.yeoljeong.tripmate.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service", path = "/internal/products")
public interface ProductFeignClient {
    @GetMapping("/{productId}/schedules/{scheduleId}")
    ApiResponse<ProductResponse> getProduct(@PathVariable("productId") UUID productId,@PathVariable("scheduleId") UUID scheduleId);
}
