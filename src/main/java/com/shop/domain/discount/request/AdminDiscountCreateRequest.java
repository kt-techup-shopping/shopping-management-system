package com.shop.domain.discount.request;

import org.jetbrains.annotations.NotNull;

import com.shop.domain.discount.model.DiscountType;

public record AdminDiscountCreateRequest(
	@NotNull
	Long productId,
	@NotNull
	Long value,
	@NotNull
	DiscountType type
) {
}
