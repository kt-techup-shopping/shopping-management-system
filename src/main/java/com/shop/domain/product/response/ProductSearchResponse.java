package com.shop.domain.product.response;

import com.querydsl.core.annotations.QueryProjection;
import com.shop.domain.discount.model.DiscountType;
import com.shop.domain.product.model.ProductStatus;

public record ProductSearchResponse (
	Long id,
	String name,
	Long price,
	ProductStatus status,
	Long discountValue,
	DiscountType discountType,
	Long discountedPrice
	){
	@QueryProjection
	public ProductSearchResponse {
	}
}
