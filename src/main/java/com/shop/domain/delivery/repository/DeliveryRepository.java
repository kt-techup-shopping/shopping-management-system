package com.shop.domain.delivery.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.domain.delivery.model.Delivery;
import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

	// Order ID로 Delivery 조회
	@EntityGraph(attributePaths = "order")
	Optional<Delivery> findByOrderId(Long orderId);

	default Delivery findByOrderIdOrThrow(Long orderId, ErrorCode errorCode) {
		return findByOrderId(orderId).orElseThrow(() -> new CustomException(errorCode));
	}

	// Order와 함께 Delivery
	@EntityGraph(attributePaths = "order")
	Optional<Delivery> findWithOrderByOrderId(Long orderId);

	default Delivery findWithOrderByOrderIdOrThrow(Long orderId, ErrorCode errorCode) {
		return findWithOrderByOrderId(orderId).orElseThrow(() -> new CustomException(errorCode));
	}

}