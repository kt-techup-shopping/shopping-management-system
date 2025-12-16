package com.shop.repository.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.shop.domain.product.ProductSort;
import com.shop.repository.product.response.AdminProductDetailQueryResponse;
import com.shop.repository.product.response.AdminProductSearchQueryResponse;
import com.shop.repository.product.response.AdminProductStockQueryResponse;
import com.shop.repository.product.response.ProductDetailQueryResponse;
import com.shop.repository.product.response.ProductSearchQueryResponse;

public interface ProductRepositoryCustom {
	Page<ProductSearchQueryResponse> getSearchList(String keyword, Long categoryId, Boolean activeOnly,
		ProductSort sort, PageRequest pageable);

	ProductDetailQueryResponse findDetailById(Long productId);

	AdminProductDetailQueryResponse findAdminDetailById(Long productId);

	Page<AdminProductSearchQueryResponse> getAdminSearchList(String keyword, Long categoryId, Boolean activeOnly,
		ProductSort sort, PageRequest pageable);

	Page<AdminProductStockQueryResponse> getStockList(String keyword, PageRequest pageable);
}
