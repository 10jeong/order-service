package com.yeoljeong.tripmate.infrastructure.external;

import com.yeoljeong.tripmate.application.client.ProductClient;
import com.yeoljeong.tripmate.application.dto.command.OrderableProductCommand;
import com.yeoljeong.tripmate.domain.exception.OrderErrorCode;
import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.infrastructure.external.dto.ProductResponse;
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
            ApiResponse<ProductResponse> feignResponse = productFeignClient.getProduct(productId, scheduleId);

            if (feignResponse == null || feignResponse.getData() == null) {
                throw new BusinessException(OrderErrorCode.PRODUCT_NOT_FOUND);
            }
            ProductResponse productResponse = feignResponse.getData();

            return new OrderableProductCommand(productResponse.productId(), productResponse.productName(), productResponse.companyName(),
                    productResponse.country(), productResponse.state(), productResponse.city(), productResponse.price(),
                    productResponse.productStatus(), productResponse.productScheduleId(), productResponse.date(), productResponse.stock(), productResponse.scheduleStatus());
        } catch (FeignException.NotFound e) {
            throw new BusinessException(OrderErrorCode.PRODUCT_NOT_FOUND);

        } catch (FeignException e) {
            throw new BusinessException(OrderErrorCode.PRODUCT_SERVICE_ERROR);
        }
    }
}
