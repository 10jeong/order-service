package com.yeoljeong.tripmate.order.presentation.dto.response;

import com.yeoljeong.tripmate.order.application.dto.result.GetOrderListResult;
import lombok.Builder;
import org.springframework.data.domain.Slice;

import java.util.List;

@Builder
public record GetOrderSliceResponse(
        List<GetOrderListResponse> content,
        int page,
        int size,
        boolean hasNext,
        boolean isFirst,
        boolean isLast
) {
    public static GetOrderSliceResponse from(Slice<GetOrderListResult> result) {
        return GetOrderSliceResponse.builder()
                .content(result.getContent().stream()
                        .map(GetOrderListResponse::from)
                        .toList())
                .page(result.getNumber())
                .size(result.getSize())
                .hasNext(result.hasNext())
                .isFirst(result.isFirst())
                .build();
    }
}
