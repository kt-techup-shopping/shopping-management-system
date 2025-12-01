package com.shop.domain.category.response;

import java.util.List;

public record AdminCategoryResponse(
	Long id,
	String name,
	Long parentId,
	List<AdminCategoryResponse> children
) {

}
