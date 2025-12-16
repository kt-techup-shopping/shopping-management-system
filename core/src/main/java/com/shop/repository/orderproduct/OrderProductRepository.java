package com.shop.repository.orderproduct;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.domain.orderproduct.OrderProduct;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

	boolean existsByProductIdAndOrderUserId(Long productId, Long userId);

	boolean existsByProductId(Long productId);
}
