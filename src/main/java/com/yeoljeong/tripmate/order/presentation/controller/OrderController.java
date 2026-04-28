package com.yeoljeong.tripmate.order.presentation.controller;

import com.yeoljeong.tripmate.order.application.dto.result.CreateOrderResult;
import com.yeoljeong.tripmate.order.application.service.command.OrderCommandService;
import com.yeoljeong.tripmate.order.presentation.dto.request.OrderRequest;
import com.yeoljeong.tripmate.order.presentation.dto.response.OrderResponse;
import com.yeoljeong.tripmate.response.ApiResponse;
import com.yeoljeong.tripmate.response.constants.CommonSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderCommandService commandService;

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@RequestHeader("X-User-Id") UUID userId, @RequestBody OrderRequest request) {
        CreateOrderResult result = commandService.createOrder(request.toCommand(userId));

        return ApiResponse.success(CommonSuccessCode.OK, OrderResponse.from(result));
    }

}
