package com.shop.domain.product.response;

import com.querydsl.core.annotations.QueryProjection;

public record AdminProductStockResponse(
	Long id,
	String name,
	Long availableStock,
	Long reservedStock,
	Long totalStock
) {
	@QueryProjection
	public AdminProductStockResponse {
	}
}
