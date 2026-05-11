package com.yeoljeong.tripmate.order.presentation.controller.internal;


import com.yeoljeong.tripmate.order.application.dto.result.OrderPlanResult;
import com.yeoljeong.tripmate.order.application.dto.result.PayableOrderResult;
import com.yeoljeong.tripmate.order.application.dto.result.WithdrawalCheckResult;
import com.yeoljeong.tripmate.order.application.service.query.OrderQueryService;
import com.yeoljeong.tripmate.order.presentation.dto.WithdrawalCheckResponse;
import com.yeoljeong.tripmate.order.presentation.dto.response.OrderPlanResponse;
import com.yeoljeong.tripmate.order.presentation.dto.response.PayableOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{orderId}")
    public OrderPlanResponse getOrderPlan(@PathVariable("orderId") UUID orderId) {
        OrderPlanResult result = queryService.getOrderPlan(orderId);

        return new OrderPlanResponse(result.orderId(), result.planUnitId());
    }

    @GetMapping("/withdrawal-check")
    public WithdrawalCheckResponse getWithdrawalCheck(@RequestParam("userId") UUID userId) {
        WithdrawalCheckResult result = queryService.getWithdrawalCheck(userId);

        return new WithdrawalCheckResponse(result.hasActiveData());
    }
}
