package com.shop.product.response;

import com.shop.domain.discount.DiscountType;
import com.shop.domain.product.ProductStatus;

public record AdminProductSearchResponse(
	Long id,
	String name,
	Long price,
	Long stock,
	ProductStatus status,
	Long discountValue,
	DiscountType discountType,
	Long discountedPrice
) {

}
