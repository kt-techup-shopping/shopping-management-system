package com.shop.domain.cartitem.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(name = "CartItemRequest.Create")
public record CartItemCreate(
	@NotNull
	Long productId,
	@NotNull
	@Min(1)
	Long quantity
) {
}