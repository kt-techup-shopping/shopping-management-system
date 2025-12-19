package com.shop.repository.order;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.CustomException;
import com.shop.ErrorCode;
import com.shop.domain.order.Order;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

	Optional<Order> findByIdAndIsDeletedFalse(Long id);

	default Order findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}

}
