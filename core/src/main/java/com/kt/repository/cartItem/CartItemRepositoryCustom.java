package com.kt.repository.cartItem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shop.domain.cartitem.response.CartItemResponse;

public interface CartItemRepositoryCustom {
	Page<CartItemResponse> search(Long userId, String keyword, Pageable pageable);
}
