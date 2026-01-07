package com.shop.repository.product.response;

import com.querydsl.core.annotations.QueryProjection;
import com.shop.domain.product.ProductStatus;

public record ProductBaseQueryResponse(Long id, String name, Long price, Long stock, ProductStatus status) {
	@QueryProjection
	public ProductBaseQueryResponse {
	}

}
