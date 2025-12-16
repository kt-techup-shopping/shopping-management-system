package com.shop.repository.product.response;

import com.querydsl.core.annotations.QueryProjection;
import com.shop.domain.discount.DiscountType;
import com.shop.domain.product.ProductStatus;

public record AdminProductSearchQueryResponse(
	Long id,
	String name,
	Long price,
	Long stock,
	ProductStatus status,
	Long discountValue,
	DiscountType discountType,
	Long discountedPrice
) {
	@QueryProjection
	public AdminProductSearchQueryResponse {
	}
}
