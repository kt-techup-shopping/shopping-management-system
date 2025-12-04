package com.shop.domain.category.response;

public record CategoryInfoResponse(
	Long id,
	String name,
	Long parentId
) {
}
