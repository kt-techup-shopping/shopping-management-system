package com.shop.domain.orderproduct.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.domain.orderproduct.model.OrderProduct;
import com.shop.domain.orderproduct.model.QOrderProduct;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

	boolean existsByProductIdAndOrderUserId(Long productId, Long userId);

	boolean existsByProductId(Long productId);
}
