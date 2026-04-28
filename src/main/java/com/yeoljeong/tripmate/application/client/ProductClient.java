package com.yeoljeong.tripmate.application.client;

import com.yeoljeong.tripmate.application.dto.command.OrderableProductCommand;

import java.util.UUID;

public interface ProductClient {
    OrderableProductCommand getProduct(UUID productId, UUID scheduleId);
}
