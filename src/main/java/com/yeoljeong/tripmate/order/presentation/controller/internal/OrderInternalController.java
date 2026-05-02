package com.yeoljeong.tripmate.order.presentation.controller.internal;


import com.yeoljeong.tripmate.order.application.dto.result.PayableOrderResult;
import com.yeoljeong.tripmate.order.application.service.query.OrderQueryService;
import com.yeoljeong.tripmate.order.presentation.dto.response.PayableOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/orders")
public class OrderInternalController {

    private final OrderQueryService queryService;

    @GetMapping("/{orderId}/payment")
    public PayableOrderResponse getPayableOrder(@PathVariable("orderId") UUID orderId) {
        PayableOrderResult result = queryService.getPayableOrder(orderId);

        return PayableOrderResponse.from(result);
    }
}
