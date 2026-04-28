package com.yeoljeong.tripmate.order.application.client;

import com.yeoljeong.tripmate.order.application.dto.command.OrderableProductCommand;

import java.util.UUID;

public interface ProductClient {
    OrderableProductCommand getProduct(UUID productId, UUID scheduleId);
}
