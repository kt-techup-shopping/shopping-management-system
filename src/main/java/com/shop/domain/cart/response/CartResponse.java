package com.shop.domain.cart.response;

import java.util.List;

import com.shop.domain.cart.model.Cart;
import com.shop.domain.cartitem.response.CartItemResponse;

public class CartResponse {

	public record Detail(
		Long cartId,
		Long userId,
		List<CartItemResponse.Detail> items,
		Long totalPrice,
		Long totalDiscountPrice,
		Long savedAmount,
		int totalItemCount,
		String message,
		boolean isEmpty
	) {

		public static Detail from(Cart cart) {
			List<CartItemResponse.Detail> items = cart.getCartItems().stream()
				.map(CartItemResponse.Detail::of)
				.toList();

			Long totalPrice = cart.getTotalPrice();
			Long totalDiscountPrice = cart.getTotalDiscountPrice();

			return new Detail(
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

		// 빈 장바구니 생성
		public static Detail empty(Long userId) {
			return new Detail(
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
}