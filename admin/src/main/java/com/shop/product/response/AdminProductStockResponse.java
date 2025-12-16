package com.shop.product.response;

public record AdminProductStockResponse(
	Long id,
	String name,
	Long availableStock,
	Long reservedStock,
	Long totalStock
) {
}
