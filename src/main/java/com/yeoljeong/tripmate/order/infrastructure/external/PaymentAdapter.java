package com.yeoljeong.tripmate.order.infrastructure.external;

import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.order.application.client.PaymentClient;
import com.yeoljeong.tripmate.order.application.dto.result.DeletableOrderResult;
import com.yeoljeong.tripmate.order.domain.exception.OrderErrorCode;
import com.yeoljeong.tripmate.order.infrastructure.external.dto.DeletableOrderResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentAdapter implements PaymentClient {

    private final PaymentFeignClient paymentFeignClient;

    @Override
    public DeletableOrderResult getDeletablePayment(UUID orderId) {
        try {
            DeletableOrderResponse deletableOrderResponse = paymentFeignClient.getDeletablePayment(orderId);

            if (deletableOrderResponse == null) {
                throw new BusinessException(OrderErrorCode.PAYMENT_SERVICE_ERROR);
            }

            return new DeletableOrderResult(deletableOrderResponse.exists(), deletableOrderResponse.paymentStatus());
        } catch (FeignException.NotFound e) {
            return new DeletableOrderResult(false, null);

        } catch (FeignException e) {
            throw new BusinessException(OrderErrorCode.PAYMENT_SERVICE_ERROR);
        }
    }
}
