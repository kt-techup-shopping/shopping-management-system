package com.shop.domain.product.request;

public record AdminProductDiscountRequest(
	Long productId,
	Integer discountPercentage
) {
}
