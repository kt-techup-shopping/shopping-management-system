package com.shop.product.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AdminProductSoldOutRequest(
	@NotNull
	@NotEmpty
	List<Long> productIds
) {
}
