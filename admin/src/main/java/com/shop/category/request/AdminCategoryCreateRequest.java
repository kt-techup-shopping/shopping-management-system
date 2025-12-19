package com.shop.category.request;

import jakarta.validation.constraints.NotBlank;

public record AdminCategoryCreateRequest(
	@NotBlank
	String name,
	Long parentCategoryId
) {
}
