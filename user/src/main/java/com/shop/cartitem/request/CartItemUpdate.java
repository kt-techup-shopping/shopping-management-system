package com.shop.cartitem.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(name = "CartItemRequest.Update")
public record CartItemUpdate(
	@NotNull
	@Min(1)
	Long quantity
) {
}