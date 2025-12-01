package com.shop.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.domain.order.model.Order;
import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;

public interface OrderRepository extends JpaRepository<Order, Long> {

	default Order findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}
}
