package com.yeoljeong.tripmate.application.client;

import com.yeoljeong.tripmate.application.dto.command.ProductInfoCommand;

import java.util.UUID;

public interface ProductClient {
    ProductInfoCommand getProduct(UUID productId, UUID scheduleId);
}
