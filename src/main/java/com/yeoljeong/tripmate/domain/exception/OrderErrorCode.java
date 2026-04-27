package com.yeoljeong.tripmate.domain.exception;

import com.yeoljeong.tripmate.exception.constants.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 상태값 변화입니다."),
    INVALID_CANCELLED_AT(HttpStatus.BAD_REQUEST, "취소 시각이 유효하지 않습니다."),
    INVALID_CANCEL_REASON(HttpStatus.BAD_REQUEST, "취소 사유가 유효하지 않습니다."),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "수량이 유효하지 않습니다."),
    INVALID_PRICE(HttpStatus.BAD_REQUEST, "가격이 유효하지 않습니다."),
    INVALID_EXPERIENCE_DATE(HttpStatus.BAD_REQUEST, "체험 예정일이 유효하지 않습니다."),
    INVALID_ID_FIELD(HttpStatus.BAD_REQUEST, "ID 필드 값이 유효하지 않습니다."),
    INVALID_TEXT_FIELD(HttpStatus.BAD_REQUEST, "필드 값이 유효하지 않습니다."),
    INVALID_ORDER(HttpStatus.BAD_REQUEST, "주문이 유효하지 않습니다."),
    INVALID_PRODUCT_INFO(HttpStatus.BAD_REQUEST, "상품 정보가 유효하지 않습니다."),
    INVALID_ORDER_ITEM(HttpStatus.BAD_REQUEST, "주문 항목이 유효하지 않습니다."),
    INVALID_ORDER_ITEM_COUNT(HttpStatus.BAD_REQUEST, "주문 항목은 주문 하나 당 한 건만 존재해야 합니다.");

    private final HttpStatus status;
    private final String description;

    @Override
    public int getCode() {
        return this.status.value();
    }

    @Override
    public String getMessage() {
        return this.description;
    }
}
