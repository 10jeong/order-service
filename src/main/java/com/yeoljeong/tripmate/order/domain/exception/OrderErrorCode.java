package com.yeoljeong.tripmate.order.domain.exception;

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
    INVALID_COUNTRY(HttpStatus.BAD_REQUEST, "국가 코드가 유효하지 않습니다."),
    INVALID_EXPERIENCE_DATE(HttpStatus.BAD_REQUEST, "체험 예정일이 유효하지 않습니다."),
    INVALID_ID_FIELD(HttpStatus.BAD_REQUEST, "ID 필드 값이 유효하지 않습니다."),
    INVALID_TEXT_FIELD(HttpStatus.BAD_REQUEST, "필드 값이 유효하지 않습니다."),
    INVALID_ORDER(HttpStatus.BAD_REQUEST, "주문이 유효하지 않습니다."),
    INVALID_PRODUCT_INFO(HttpStatus.BAD_REQUEST, "상품 정보가 유효하지 않습니다."),
    INVALID_ORDER_ITEM(HttpStatus.BAD_REQUEST, "주문 항목이 유효하지 않습니다."),
    INVALID_ORDER_ITEM_COUNT(HttpStatus.BAD_REQUEST, "주문 항목은 주문 하나 당 한 건 존재해야 합니다."),
    REQUIRED_ORDER_ITEM_INFO(HttpStatus.BAD_REQUEST, "planUnitId, productId, scheduleId는 필수입니다."),
    INVALID_ORDER_QUANTITY(HttpStatus.BAD_REQUEST, "quantity는 1 이상이어야 합니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    PRODUCT_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "상품 서비스 호출 에러입니다."),
    PRODUCT_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "주문 가능한 상품이 아닙니다."),
    SCHEDULE_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "주문 가능한 일정이 아닙니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "상품 재고가 부족합니다."),
    ALREADY_ORDERED_PLAN_UNIT(HttpStatus.CONFLICT, "이미 주문한 단위 일정입니다."),
    REQUIRED_USER_ID(HttpStatus.BAD_REQUEST, "userId는 필수입니다.");

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
