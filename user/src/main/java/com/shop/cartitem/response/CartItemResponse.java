package com.shop.domain.cartitem.response;

import com.shop.domain.cartitem.model.CartItem;

public record CartItemResponse(
	Long cartItemId,
	Long productId,
	String productName,
	String description,
	String color,
	String categories,
	Long productPrice,
	Long discountPrice,
	Long quantity,
	Long totalPrice,
	Long totalDiscountPrice,
	boolean isAvailable,
	boolean isSoldOut
) {

	// status를 메서드로 제공 (파생 정보)
	public String status() {
		if (!isAvailable) {
			return "판매중지";
		}
		if (isSoldOut) {
			return "품절";
		}
		return "정상";
	}

	public static CartItemResponse of(CartItem cartItem) {
		return new CartItemResponse(
			cartItem.getId(),
			cartItem.getProduct().getId(),
			cartItem.getProduct().getName(),
			cartItem.getProduct().getDescription(),
			cartItem.getProduct().getColor(),
			cartItem.getProduct().getCategory().getName(),
			cartItem.getProduct().getPrice(),
			cartItem.getProduct().getDiscountPrice(),
			cartItem.getQuantity(),
			cartItem.getTotalPrice(),
			cartItem.getTotalDiscountPrice(),
			cartItem.isAvailable(),
			cartItem.isSoldOut()
		);
	}
}
