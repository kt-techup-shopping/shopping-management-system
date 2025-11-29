package com.shop.domain.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.shop.domain.product.request.ProductSort;
import com.shop.domain.product.response.AdminProductDetailQueryResponse;
import com.shop.domain.product.response.AdminProductSearchResponse;
import com.shop.domain.product.response.AdminProductStockResponse;
import com.shop.domain.product.response.ProductDetailQueryResponse;
import com.shop.domain.product.response.ProductSearchResponse;

public interface ProductRepositoryCustom {
	Page<ProductSearchResponse> getSearchList(String keyword, Long categoryId, Boolean activeOnly,
		ProductSort sort, PageRequest pageable);

	ProductDetailQueryResponse findDetailById(Long productId);
	AdminProductDetailQueryResponse findAdminDetailById(Long productId);

	Page<AdminProductSearchResponse> getAdminSearchList(String keyword, Long categoryId, Boolean activeOnly,
		ProductSort sort, PageRequest pageable);

	Page<AdminProductStockResponse> getStockList(PageRequest pageable);
}
