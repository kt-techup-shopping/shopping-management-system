package com.shop.domain.product.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.category.repository.CategoryRepository;
import com.shop.domain.category.service.CategoryService;
import com.shop.domain.product.model.Product;
import com.shop.domain.product.model.ProductStatus;
import com.shop.domain.product.repository.ProductRepository;
import com.shop.domain.product.request.ProductSort;
import com.shop.domain.product.response.AdminProductDetailResponse;
import com.shop.domain.product.response.AdminProductSearchResponse;
import com.shop.global.common.ErrorCode;
import com.shop.global.common.Lock;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final CategoryService categoryService;

	// 관리자 상품 등록
	@Transactional
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
	public Page<AdminProductSearchResponse> getAdminSearchList(String keyword, Long categoryId, Boolean activeOnly,
		String sort, PageRequest pageable) {
		return productRepository.getAdminSearchList(keyword, categoryId, activeOnly, ProductSort.from(sort), pageable);
	}

	public AdminProductDetailResponse getAdminDetailById(Long id) {
		var product = productRepository.findAdminDetailById(id);
		var categoryList = categoryService.getCategoryHierarchy(product.category());

		return new AdminProductDetailResponse(
			product.id(),
			product.name(),
			product.price(),
			product.description(),
			product.color(),
			product.stock(),
			product.status(),
			product.discountValue(),
			product.discountType(),
			product.discountedPrice(),
			categoryList
		);
	}

	@Lock(key = Lock.Key.PRODUCT, index = 0, waitTime = 1000, leaseTime = 500, timeUnit = TimeUnit.MILLISECONDS)
	public void updateDetail(Long id, String name, Long price, String description, String color, Long deltaStock,
		String status, Long categoryId) {
		var product = productRepository.findByIdOrThrow(id);
		var category = categoryRepository.findByIdOrThrow(categoryId, ErrorCode.NOT_FOUND_CATEGORY);

		product.update(
			name,
			price,
			description,
			color,
			deltaStock,
			ProductStatus.from(status),
			category
		);
	}
}
