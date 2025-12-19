package com.shop.cart.response;

import java.util.List;

import com.shop.cartitem.response.CartItemResponse;
import com.shop.domain.cart.Cart;

public record CartResponse(
	Long cartId,
	Long userId,
	List<CartItemResponse> items,
	Long totalPrice,
	Long totalDiscountPrice,
	Long savedAmount,
	int totalItemCount,
	String message,
	boolean isEmpty
) {

	public static CartResponse from(Cart cart) {
		List<CartItemResponse> items = cart.getCartItems().stream()
			.map(CartItemResponse::of)
			.toList();

		Long totalPrice = cart.getTotalPrice();
		Long totalDiscountPrice = cart.getTotalDiscountPrice();

		return new CartResponse(
			cart.getId(),
			cart.getUser().getId(),
			items,
			totalPrice,
			totalDiscountPrice,
			totalPrice - totalDiscountPrice,
			cart.getTotalItemCount(),
			items.isEmpty() ? "장바구니가 비어있습니다." : null,
			items.isEmpty()
		);
	}

	// 빈 장바구니
	public static CartResponse empty(Long userId) {
		return new CartResponse(
			null,
			userId,
			List.of(),
			0L,
			0L,
			0L,
			0,
			"장바구니가 비어있습니다.",
			true
		);
	}
}
