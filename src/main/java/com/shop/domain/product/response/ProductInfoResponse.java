package com.shop.domain.product.response;

public record ProductInfoResponse(
	Long id,
	String name,
	Long price,
	String description
) {

}
