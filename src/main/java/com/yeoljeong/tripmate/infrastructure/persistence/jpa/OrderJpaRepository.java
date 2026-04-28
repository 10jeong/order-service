package com.yeoljeong.tripmate.infrastructure.persistence.jpa;

import com.yeoljeong.tripmate.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {

}
