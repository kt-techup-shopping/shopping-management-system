package com.shop.category.response;

public record CategoryInfoResponse(
	Long id,
	String name,
	Long parentId
) {
}
