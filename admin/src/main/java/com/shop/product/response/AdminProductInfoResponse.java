package com.shop.product.response;

public record AdminProductInfoResponse(
	Long id,
	String name,
	Long price,
	String description,
	Long stock
) {

}
