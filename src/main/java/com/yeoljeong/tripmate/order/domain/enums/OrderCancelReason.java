package com.yeoljeong.tripmate.order.domain.enums;

import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.order.domain.exception.OrderErrorCode;

public enum OrderCancelReason {
    PLAN_PARTICIPANT_QUIT,
    PLAN_PARTICIPANT_EXCEEDED,
    PRODUCT_STOCK_SHORTAGE,
    PAYMENT_TIMEOUT;

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
