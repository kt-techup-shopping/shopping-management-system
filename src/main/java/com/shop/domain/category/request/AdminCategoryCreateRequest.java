package com.shop.domain.category.request;

public record AdminCategoryCreateRequest(
	String name,
	Long parentCategoryId
) {
}
