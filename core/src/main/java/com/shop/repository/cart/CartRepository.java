package com.shop.repository.cart;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.domain.cart.Cart;

import jakarta.validation.constraints.NotNull;

public interface CartRepository extends JpaRepository<Cart, Long> {

	// 기본 Cart 조회
	Optional<Cart> findByUserId(Long userId);

	// CartItem을 함께 조회
	@NotNull
	@EntityGraph(attributePaths = "cartItems")
	Optional<Cart> findWithCartItemsByUserId(Long userId);

	// CartItem, Product를 함께 조회
	@NotNull
	@EntityGraph(attributePaths = {"cartItems", "cartItems.product"})
	Optional<Cart> findWithCartItemsAndProductsByUserId(Long userId);

	// 장바구니 존재 여부 확인
	boolean existsByUserId(Long userId);

}

