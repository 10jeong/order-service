package com.yeoljeong.tripmate.order.infrastructure.external;

import com.yeoljeong.tripmate.order.application.client.ProductClient;
import com.yeoljeong.tripmate.order.application.dto.command.OrderableProductCommand;
import com.yeoljeong.tripmate.order.domain.exception.OrderErrorCode;
import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.order.infrastructure.external.dto.ProductResponse;
import com.yeoljeong.tripmate.response.ApiResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductAdapter implements ProductClient {

    private final ProductFeignClient productFeignClient;

    @Override
    public OrderableProductCommand getProduct(UUID productId, UUID scheduleId) {
        try {
            ProductResponse productResponse = productFeignClient.getProduct(productId, scheduleId);

            if (productResponse == null) {
                throw new BusinessException(OrderErrorCode.PRODUCT_NOT_FOUND);
            }

            return new OrderableProductCommand(productResponse.productId(), productResponse.productName(),
                    productResponse.country(), productResponse.state(), productResponse.city(), productResponse.price(),
                    productResponse.productStatus(), productResponse.scheduleId(), productResponse.date(), productResponse.stock(), productResponse.scheduleStatus());
        } catch (FeignException.NotFound e) {
            throw new BusinessException(OrderErrorCode.PRODUCT_NOT_FOUND);

        } catch (FeignException e) {
            throw new BusinessException(OrderErrorCode.PRODUCT_SERVICE_ERROR);
        }
    }
}
