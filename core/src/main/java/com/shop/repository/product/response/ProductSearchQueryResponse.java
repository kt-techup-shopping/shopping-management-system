package com.shop.repository.product.response;

import com.querydsl.core.annotations.QueryProjection;
import com.shop.domain.discount.DiscountType;
import com.shop.domain.product.ProductStatus;

public record ProductSearchQueryResponse(
	Long id,
	String name,
	Long price,
	ProductStatus status,
	Long discountValue,
	DiscountType discountType,
	Long discountedPrice
	){
	@QueryProjection
	public ProductSearchQueryResponse {
	}
}
