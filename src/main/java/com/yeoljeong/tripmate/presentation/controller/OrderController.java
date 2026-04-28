package com.yeoljeong.tripmate.presentation.controller;

import com.yeoljeong.tripmate.application.dto.result.CreateOrderResult;
import com.yeoljeong.tripmate.application.service.command.OrderCommandService;
import com.yeoljeong.tripmate.presentation.dto.request.OrderRequest;
import com.yeoljeong.tripmate.presentation.dto.response.OrderResponse;
import com.yeoljeong.tripmate.response.ApiResponse;
import com.yeoljeong.tripmate.response.constants.CommonSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderCommandService commandService;

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        CreateOrderResult result = commandService.createOrder(request.toCommand());

        return ApiResponse.success(CommonSuccessCode.OK, OrderResponse.from(result));
    }

}
