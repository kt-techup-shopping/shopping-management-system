package com.shop.repository.product.response;

import com.querydsl.core.annotations.QueryProjection;
import com.shop.domain.category.Category;
import com.shop.domain.discount.DiscountType;
import com.shop.domain.product.ProductStatus;

public record AdminProductDetailQueryResponse(
	Long id,
	String name,
	Long price,
	String description,
	String color,
	Long stock,
	ProductStatus status,
	Category category,
	Long discountValue,
	DiscountType discountType,
	Long discountedPrice
) {
	@QueryProjection
	public AdminProductDetailQueryResponse {
	}
}
