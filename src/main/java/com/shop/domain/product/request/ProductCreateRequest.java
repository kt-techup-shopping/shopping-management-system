package com.shop.domain.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductCreateRequest(
	@NotBlank
	String name,
	@NotNull
	Long price,
	@NotNull
	String description,
	@NotNull
	String color,
	@NotNull
	Long stock,
	@NotNull
	Long categoryId) {

}
