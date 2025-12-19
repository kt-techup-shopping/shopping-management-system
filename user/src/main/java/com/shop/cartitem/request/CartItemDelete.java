package com.shop.cartitem.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@Schema(name = "CartItemRequest.Delete")
public record CartItemDelete(
	@NotEmpty
	List<Long> cartItemId
) {
}
