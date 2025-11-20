package com.shop.domain.orderproduct.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.domain.orderproduct.model.OrderProduct;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
}
