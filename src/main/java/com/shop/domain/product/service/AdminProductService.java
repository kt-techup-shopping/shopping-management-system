package com.shop.domain.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.category.repository.CategoryRepository;
import com.shop.domain.product.model.Product;
import com.shop.domain.product.repository.ProductRepository;
import com.shop.domain.product.request.ProductSort;
import com.shop.domain.product.response.AdminProductSearchResponse;
import com.shop.domain.product.response.ProductSearchResponse;
import com.shop.global.common.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

	// 관리자 상품 등록
	public void create(String name, Long price, Long stock, String description, String color, Long categoryId) {
		var category = categoryRepository.findByIdOrThrow(categoryId, ErrorCode.NOT_FOUND_CATEGORY);
		productRepository.save(new Product(
			name,
			price,
			stock,
			description,
			color,
			category
		));
	}

	// 관리자 상품 목록 조회
	public Page<AdminProductSearchResponse> getAdminSearchList(String keyword, Long categoryId, Boolean activeOnly, String sort, PageRequest pageable) {

		return productRepository.getAdminSearchList(keyword, categoryId, activeOnly, ProductSort.from(sort), pageable);
	}
}
