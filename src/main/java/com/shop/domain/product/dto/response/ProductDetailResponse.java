package com.shop.domain.product.dto.response;

import java.util.List;

import com.shop.domain.category.dto.CategoryDetailResponse;
import com.shop.domain.product.model.ProductStatus;

public record ProductDetailResponse (
	Long id,
	String name,
	Long price,
	String description,
	String color,
	ProductStatus status,
	List<CategoryDetailResponse> categories
){
}
