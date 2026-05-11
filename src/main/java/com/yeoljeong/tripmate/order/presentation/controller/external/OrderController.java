package com.yeoljeong.tripmate.order.presentation.controller.external;

import com.yeoljeong.tripmate.auth.annotation.LoginUser;
import com.yeoljeong.tripmate.auth.context.UserContext;
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
    public ApiResponse<OrderResponse> createOrder(@LoginUser UserContext userContext, @RequestBody OrderRequest request) {
        OrderResult result = commandService.createOrder(request.toCommand(userContext.userId()));

        return ApiResponse.success(CommonSuccessCode.OK, OrderResponse.from(result));
    }

    // 주문 단건 조회
    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrder(@LoginUser UserContext userContext, @PathVariable("orderId") UUID orderId) {
        OrderResult result = queryService.getOrder(orderId, userContext.userId());
        return ApiResponse.success(CommonSuccessCode.OK, OrderResponse.from(result));
    }

    // 주문 목록 조회
    @GetMapping
    public ApiResponse<GetOrderSliceResponse> getOrders(@LoginUser UserContext userContext,
                                                        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Slice<GetOrderListResult> result = queryService.getOrders(userContext.userId(), pageable);
        return ApiResponse.success(CommonSuccessCode.OK, GetOrderSliceResponse.from(result));
    }
}
