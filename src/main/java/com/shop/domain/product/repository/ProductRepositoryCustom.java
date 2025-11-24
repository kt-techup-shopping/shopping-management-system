package com.shop.domain.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.shop.domain.order.response.OrderResponse;
import com.shop.domain.product.dto.response.ProductSearchResponse;

public interface ProductRepositoryCustom {
	Page<ProductSearchResponse> search(String keyword, PageRequest pageable);
}
