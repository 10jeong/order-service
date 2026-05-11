package com.yeoljeong.tripmate.order.domain.enums;

import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.order.domain.exception.OrderErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderCancelReason {
    PLAN_PARTICIPANT_QUIT("일정 탈퇴"),
    PLAN_PARTICIPANT_EXCEEDED("일정 인원 초과"),
    PRODUCT_STOCK_SHORTAGE("상품 재고 부족"),
    PAYMENT_TIMEOUT("결제 시간 초과");

    private final String description;

    public static OrderCancelReason from(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(OrderErrorCode.INVALID_CANCEL_REASON);
        }

        try {
            return OrderCancelReason.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(OrderErrorCode.INVALID_CANCEL_REASON);
        }
    }
}
