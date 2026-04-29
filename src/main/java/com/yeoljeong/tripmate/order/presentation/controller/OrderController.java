package com.yeoljeong.tripmate.order.presentation.controller;

import com.yeoljeong.tripmate.order.application.dto.result.OrderResult;
import com.yeoljeong.tripmate.order.application.dto.result.GetOrderListResult;
import com.yeoljeong.tripmate.order.application.service.command.OrderCommandService;
import com.yeoljeong.tripmate.order.application.service.query.OrderQueryService;
import com.yeoljeong.tripmate.order.presentation.dto.request.OrderRequest;
import com.yeoljeong.tripmate.order.presentation.dto.response.GetOrderSliceResponse;
import com.yeoljeong.tripmate.order.presentation.dto.response.OrderResponse;
import com.yeoljeong.tripmate.response.ApiResponse;
import com.yeoljeong.tripmate.response.constants.CommonSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderCommandService commandService;
    private final OrderQueryService queryService;

    // 주문 생성
    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@RequestHeader("X-User-Id") UUID userId, @RequestBody OrderRequest request) {
        OrderResult result = commandService.createOrder(request.toCommand(userId));

        return ApiResponse.success(CommonSuccessCode.OK, OrderResponse.from(result));
    }

    // 주문 단건 조회
    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrder(@RequestHeader("X-User-Id") UUID userId, @PathVariable("orderId") UUID orderId) {
        OrderResult result = queryService.getOrder(orderId, userId);
        return ApiResponse.success(CommonSuccessCode.OK, OrderResponse.from(result));
    }

    // 주문 목록 조회
    @GetMapping
    public ApiResponse<GetOrderSliceResponse> getOrders(@RequestHeader("X-User-Id") UUID userId,
                                                        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Slice<GetOrderListResult> result = queryService.getOrders(userId, pageable);
        return ApiResponse.success(CommonSuccessCode.OK, GetOrderSliceResponse.from(result));
    }
}
