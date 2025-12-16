package com.shop.product.request;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import jakarta.validation.constraints.NotEmpty;

public record ProductSoldOutRequest(
	@NotNull
	@NotEmpty
	List<Long> productIds
) {
}
