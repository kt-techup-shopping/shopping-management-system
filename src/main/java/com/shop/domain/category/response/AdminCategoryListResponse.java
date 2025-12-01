package com.shop.domain.category.response;

import java.util.List;

public record AdminCategoryListResponse(
	List<AdminCategoryResponse> categories
) {}