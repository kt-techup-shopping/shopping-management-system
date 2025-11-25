package com.shop.domain.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.shop.domain.product.request.ProductSort;
import com.shop.domain.product.response.AdminProductSearchResponse;
import com.shop.domain.product.response.ProductDetailProjection;
import com.shop.domain.product.response.ProductSearchResponse;

public interface ProductRepositoryCustom {
	Page<ProductSearchResponse> getSearchList(String keyword, Long categoryId, Boolean activeOnly,
		ProductSort sort, PageRequest pageable);

	ProductDetailProjection findDetailById(Long productId);

	Page<AdminProductSearchResponse> getAdminSearchList(String keyword, Long categoryId, Boolean activeOnly,
		ProductSort sort, PageRequest pageable);

}
