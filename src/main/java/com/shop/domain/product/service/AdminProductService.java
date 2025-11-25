package com.shop.domain.product.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.category.repository.CategoryRepository;
import com.shop.domain.product.model.Product;
import com.shop.domain.product.repository.ProductRepository;
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
}
