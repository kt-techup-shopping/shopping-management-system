package com.shop.domain.category.request;

import jakarta.validation.constraints.NotBlank;

public record AdminCategoryCreateRequest(
	@NotBlank
	String name,
	Long parentCategoryId
) {
}
