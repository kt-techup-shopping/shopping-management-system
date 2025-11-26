package com.shop.domain.cartitem.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@Schema(name = "CartItemRequest.Delete")
public class CartItemDelete {
	@NotEmpty
	private List<Long> cartItemId;
}
