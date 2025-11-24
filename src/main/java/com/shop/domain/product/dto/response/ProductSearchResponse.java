package com.shop.domain.product.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.shop.domain.product.model.ProductStatus;

public record ProductSearchResponse (
	Long id,
	String name,
	Long price,
	Long stock,
	ProductStatus status
	){
	@QueryProjection
	public ProductSearchResponse {
	}
}
