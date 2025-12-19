package com.shop.order.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record OrderDeleteRequest(
	@NotNull
	List<Long> productIds,
	@NotNull
	Long orderId
) {
}
