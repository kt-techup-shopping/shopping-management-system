package com.shop.discount.request;

import com.shop.domain.discount.DiscountType;

import jakarta.validation.constraints.NotNull;

public record AdminDiscountCreateRequest(
	@NotNull
	Long productId,
	@NotNull
	Long value,
	@NotNull
	DiscountType type
) {
}
