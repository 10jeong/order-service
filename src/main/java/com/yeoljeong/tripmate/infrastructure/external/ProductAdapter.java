package com.yeoljeong.tripmate.infrastructure.external;

import com.yeoljeong.tripmate.application.client.ProductClient;
import com.yeoljeong.tripmate.application.dto.command.ProductInfoCommand;
import com.yeoljeong.tripmate.domain.exception.OrderErrorCode;
import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.infrastructure.external.dto.ProductResponse;
import com.yeoljeong.tripmate.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductAdapter implements ProductClient {

    private final ProductFeignClient productFeignClient;

    @Override
    public ProductInfoCommand getProduct(UUID productId, UUID scheduleId) {
        ApiResponse<ProductResponse> feignResponse = productFeignClient.getProduct(productId, scheduleId);
        ProductResponse productResponse = feignResponse.getData();

        if (productResponse == null) {
            throw new BusinessException(OrderErrorCode.PRODUCT_NOT_FOUND);
        }

        return new ProductInfoCommand(productResponse.productId(), productResponse.productName(), productResponse.companyName(),
                productResponse.country(), productResponse.state(), productResponse.city(), productResponse.price(),
                productResponse.productStatus(), productResponse.productScheduleId(), productResponse.date(), productResponse.stock(), productResponse.scheduleStatus());
    }
}
