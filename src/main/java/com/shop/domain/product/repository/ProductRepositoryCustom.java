package com.shop.domain.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.shop.domain.product.response.ProductDetailProjection;
import com.shop.domain.product.response.ProductSearchResponse;

public interface ProductRepositoryCustom {
	Page<ProductSearchResponse> search(String keyword, Long categoryId, Boolean activeOnly, String sort, PageRequest pageable);
	ProductDetailProjection findDetailById(Long productId);
}
