package com.shop.domain.cartitem.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CartItemRequest {

	@Getter
	@NoArgsConstructor
	@Schema(name = "CartItemRequest.Create")
	public static class Create {
		@NotNull
		private Long productId;
		@NotNull
		@Min(1)
		private Long quantity;
	}

	@Getter
	@NoArgsConstructor
	@Schema(name = "CartItemRequest.Update")
	public static class Update {
		@NotNull
		@Min(1)
		private Long quantity;
	}

	@Getter
	@NoArgsConstructor
	@Schema(name = "CartItemRequest.Delete")
	public static class Delete {
		@NotEmpty
		private List<Long> cartItemId;
	}
}
