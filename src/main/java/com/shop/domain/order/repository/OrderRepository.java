package com.shop.domain.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.domain.order.model.Order;
import com.shop.global.common.ErrorCode;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

	Optional<Order> findByIdAndIsDeletedFalse(Long id);

	default Order findByIdOrThrow(Long orderId, ErrorCode errorCode){
		return findByIdAndIsDeletedFalse(orderId)
			.orElseThrow(() -> new RuntimeException(errorCode.getMessage()));
	};
}
