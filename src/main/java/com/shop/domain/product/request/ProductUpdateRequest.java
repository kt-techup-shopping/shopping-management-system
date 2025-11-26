package com.shop.domain.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductUpdateRequest(
	@NotBlank
	String name,
	@NotNull
	Long price,
	@NotBlank
	String description,
	@NotBlank
	String color,
	@NotNull
	Long deltaStock,
	@NotBlank
	String status,
	@NotNull
	Long categoryId
) {

}
