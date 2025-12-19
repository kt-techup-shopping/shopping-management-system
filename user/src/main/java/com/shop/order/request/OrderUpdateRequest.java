package com.shop.order.request;

import java.util.Map;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderUpdateRequest(
	@NotNull
	Map<Long, @Min(1) Long> productQuantity, // key: productId, value: quantity
	@NotBlank
	String receiverName,
	@NotBlank
	String receiverAddress,
	@NotBlank
	String receiverMobile,
	@NotNull
	Long orderId
) {
}
