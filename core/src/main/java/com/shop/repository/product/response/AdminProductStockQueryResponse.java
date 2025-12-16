package com.shop.repository.product.response;

import com.querydsl.core.annotations.QueryProjection;

public record AdminProductStockQueryResponse(
	Long id,
	String name,
	Long availableStock,
	Long reservedStock,
	Long totalStock
) {
	@QueryProjection
	public AdminProductStockQueryResponse {
	}
}
