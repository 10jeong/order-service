package com.yeoljeong.tripmate.domain.repository;

import com.yeoljeong.tripmate.domain.entity.Order;

public interface OrderRepository {
    Order save(Order order);
}
