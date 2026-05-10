package com.yeoljeong.tripmate.order.infrastructure.external;

import com.yeoljeong.tripmate.order.infrastructure.external.dto.DeletableOrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "payment-service", path = "/internal/payments")
public interface PaymentFeignClient {
    @GetMapping("/orders/{orderId}")
    DeletableOrderResponse getDeletablePayment(@PathVariable("orderId") UUID orderId);
}
