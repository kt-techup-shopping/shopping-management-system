package com.shop.product.response;

import com.shop.domain.discount.DiscountType;
import com.shop.domain.product.ProductStatus;

public record ProductSearchResponse (
	Long id,
	String name,
	Long price,
	ProductStatus status,
	Long discountValue,
	DiscountType discountType,
	Long discountedPrice
	){
}
