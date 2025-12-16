package com.shop.product.response;

import java.util.List;

import com.shop.category.response.CategoryDetailResponse;
import com.shop.domain.discount.DiscountType;
import com.shop.domain.product.ProductStatus;

public record ProductDetailResponse (
	Long id,
	String name,
	Long price,
	String description,
	String color,
	ProductStatus status,
	Long discountValue,
	DiscountType discountType,
	Long discountedPrice,
	List<CategoryDetailResponse> categories
){
}
