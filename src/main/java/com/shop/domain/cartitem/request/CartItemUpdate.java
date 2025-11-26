package com.shop.domain.cartitem.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "CartItemRequest.Update")
public class CartItemUpdate {
	@NotNull
	@Min(1)
	private Long quantity;
}