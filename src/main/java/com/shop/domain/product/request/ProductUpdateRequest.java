package com.shop.domain.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductUpdateRequest(
	@NotBlank
	String name,
	@NotNull
	Long price,
	@NotNull
	String description,
	@NotNull
	String color,
	@NotNull
	Long deltaStock,
	@NotNull
	String status,
	@NotNull
	Long categoryId
) {

}
