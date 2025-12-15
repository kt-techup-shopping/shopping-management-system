package com.shop.repository.cartItem;

import org.springframework.data.domain.Pageable;


public interface CartItemRepositoryCustom {
	Page<CartItemResponse> search(Long userId, String keyword, Pageable pageable);
}
