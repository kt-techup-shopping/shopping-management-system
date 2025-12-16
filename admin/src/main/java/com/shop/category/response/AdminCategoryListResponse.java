package com.shop.category.response;

import java.util.List;

public record AdminCategoryListResponse(
	List<AdminCategoryResponse> categories
) {}