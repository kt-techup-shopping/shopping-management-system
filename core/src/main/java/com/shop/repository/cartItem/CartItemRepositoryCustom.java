package com.shop.repository.cartItem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shop.repository.cartItem.response.CartItemQueryResponse;

public interface CartItemRepositoryCustom {
	Page<CartItemQueryResponse> search(Long userId, String keyword, Pageable pageable);
}
