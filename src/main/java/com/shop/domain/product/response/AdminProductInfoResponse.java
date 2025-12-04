package com.shop.domain.product.response;

public record AdminProductInfoResponse(
	Long id,
	String name,
	Long price,
	String description,
	Long stock
) {

}
