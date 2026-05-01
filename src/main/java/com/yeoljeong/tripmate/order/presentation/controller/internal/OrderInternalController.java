package com.yeoljeong.tripmate.order.presentation.controller.internal;


import com.yeoljeong.tripmate.order.application.dto.result.PayableOrderResult;
import com.yeoljeong.tripmate.order.application.service.query.OrderQueryService;
import com.yeoljeong.tripmate.order.presentation.dto.response.PayableOrderResponse;
import com.yeoljeong.tripmate.response.ApiResponse;
import com.yeoljeong.tripmate.response.constants.CommonSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/orders")
public class OrderInternalController {

    private final OrderQueryService queryService;

    @GetMapping("{orderId}/payments")
    public ApiResponse<PayableOrderResponse> getPayableOrder(@RequestHeader("X-User-Id") UUID userId, @PathVariable("orderId") UUID orderId) {
        PayableOrderResult result = queryService.getPayableOrder(orderId);

        return ApiResponse.success(CommonSuccessCode.OK, PayableOrderResponse.from(result));
    }
}
