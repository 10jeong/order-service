package com.yeoljeong.tripmate.order.application.client;

import com.yeoljeong.tripmate.order.application.dto.result.DeletableOrderResult;

import java.util.UUID;

public interface PaymentClient {
    DeletableOrderResult getDeletablePayment(UUID orderId);
}
