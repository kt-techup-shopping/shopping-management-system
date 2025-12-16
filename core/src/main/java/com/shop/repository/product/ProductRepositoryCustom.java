package com.shop.repository.product;

import org.springframework.data.domain.PageRequest;

import com.shop.domain.product.ProductSort;
import com.shop.repository.product.response.ProductDetailQueryResponse;

public interface ProductRepositoryCustom {
	Page<ProductSearchResponse> getSearchList(String keyword, Long categoryId, Boolean activeOnly,
		ProductSort sort, PageRequest pageable);

	ProductDetailQueryResponse findDetailById(Long productId);

	AdminProductDetailQueryResponse findAdminDetailById(Long productId);

	Page<AdminProductSearchResponse> getAdminSearchList(String keyword, Long categoryId, Boolean activeOnly,
		ProductSort sort, PageRequest pageable);

	Page<AdminProductStockResponse> getStockList(String keyword, PageRequest pageable);
}
