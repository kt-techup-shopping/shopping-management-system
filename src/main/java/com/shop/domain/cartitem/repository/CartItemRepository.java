package com.shop.domain.cartitem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.domain.cartitem.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long>, CartItemRepositoryCustom {

	@EntityGraph(attributePaths = "product")
	Optional<CartItem> findWithProductByCartUserIdAndProductId(Long userId, Long productId);

	@EntityGraph(attributePaths = "product")
	Optional<CartItem> findWithProductByCartUserIdAndId(Long userId, Long cartItemId);

	Optional<CartItem> findByCartUserIdAndId(Long userId, Long cartItemId);

	void deleteByCartUserIdAndIdIn(Long userId, java.util.List<Long> cartItemIds);

	void deleteAllByCartUserId(Long userId);

	void deleteByProductId(Long productId);

	Long countByCartUserId(Long userId);
}