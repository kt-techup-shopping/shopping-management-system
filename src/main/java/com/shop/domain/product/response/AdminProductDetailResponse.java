package com.shop.domain.product.response;

import java.util.List;

import com.shop.domain.category.response.CategoryDetailResponse;
import com.shop.domain.discount.model.DiscountType;
import com.shop.domain.product.model.ProductStatus;

public record AdminProductDetailResponse(
	Long id,
	String name,
	Long price,
	String description,
	String color,
	Long stock,
	ProductStatus status,
	Long discountValue,
	DiscountType discountType,
	Long discountedPrice,
	List<CategoryDetailResponse> categories
) {
}
