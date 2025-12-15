package com.kt.repository.orderproduct;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

	boolean existsByProductIdAndOrderUserId(Long productId, Long userId);

	boolean existsByProductId(Long productId);
}
