package com.shop.product.response;

import java.util.List;

import com.shop.category.response.AdminCategoryDetailResponse;
import com.shop.domain.discount.DiscountType;
import com.shop.domain.product.ProductStatus;

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
	List<AdminCategoryDetailResponse> categories
) {
}
