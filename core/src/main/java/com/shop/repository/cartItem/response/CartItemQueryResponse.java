package com.shop.repository.cartItem.response;

import com.shop.domain.cartItem.CartItem;
import com.shop.domain.category.Category;

public record CartItemQueryResponse(
	Long cartItemId,
	Long productId,
	String productName,
	String description,
	String color,
	Category categories,
	Long productPrice,
	Long discountPrice,
	Long quantity,
	Long totalPrice,
	Long totalDiscountPrice,
	boolean isAvailable,
	CartItem cartItem
) {
	public static CartItemQueryResponse of(CartItem cartItem) {
		return new CartItemQueryResponse(
			cartItem.getId(),
			cartItem.getProduct().getId(),
			cartItem.getProduct().getName(),
			cartItem.getProduct().getDescription(),
			cartItem.getProduct().getColor(),
			cartItem.getProduct().getCategory(),
			cartItem.getProduct().getPrice(),
			cartItem.getProduct().getDiscountPrice(),
			cartItem.getQuantity(),
			cartItem.getTotalPrice(),
			cartItem.getTotalDiscountPrice(),
			cartItem.isAvailable(),
			cartItem
		);
	}
}
