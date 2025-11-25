package com.shop.domain.product.response;

import com.querydsl.core.annotations.QueryProjection;
import com.shop.domain.category.model.Category;
import com.shop.domain.discount.model.DiscountType;
import com.shop.domain.product.model.ProductStatus;

public record ProductDetailQueryResponse(
	Long id,
	String name,
	Long price,
	String description,
	String color,
	ProductStatus status,
	Category category,
	Long discountValue,
	DiscountType discountType,
	Long discountedPrice
) {
	@QueryProjection
	public ProductDetailQueryResponse {
	}
}
