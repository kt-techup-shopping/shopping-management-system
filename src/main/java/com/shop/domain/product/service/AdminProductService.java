package com.shop.domain.product.service;

import java.util.List;
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
import com.shop.domain.product.response.AdminProductStockResponse;
import com.shop.global.common.ErrorCode;
import com.shop.global.common.Lock;
import com.shop.global.common.Preconditions;

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

	// 관리자 상품 상세 조회
	public AdminProductDetailResponse getAdminDetailById(Long id) {
		var	isExisted = productRepository.existsById(id);
		Preconditions.validate(isExisted, ErrorCode.NOT_FOUND_PRODUCT);
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

	// 관리자 상품 정보 수정
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

	// 관리자 상품 상태 비활성화
	@Transactional
	public void updateActivated(Long id) {
		var product = productRepository.findByIdOrThrow(id);
		product.activate();
	}

	// 관리자 상품 상태 비활성화
	@Transactional
	public void updateInActivated(Long id) {
		var product = productRepository.findByIdOrThrow(id);
		product.inActivate();
	}

	// 관리자 상품 상태 품절 토글
	@Transactional
	public void updateSoldOutToggle(Long id) {
		var product = productRepository.findByIdOrThrow(id);
		product.toggleSoldOut();
	}

	// 관리자 상품 상태 다중 품절
	@Transactional
	public void updateSoldOutList(List<Long> ids) {
		// 상품 리스트 유효성 검사
		var products = productRepository.findAllById(ids);
		Preconditions.validate(products.size() == ids.size(), ErrorCode.NOT_FOUND_PRODUCT);

		// 품절 업데이트
		products.forEach(Product::soldOut);
	}

	// 관리자 상품 재고 목록 조회
	public Page<AdminProductStockResponse> getStockList(String keyword, PageRequest paging) {
		return productRepository.getStockList(keyword, paging);
	}

	// 관리자 상품 재고 수정
	@Lock(key = Lock.Key.PRODUCT, index = 0, waitTime = 1000, leaseTime = 500, timeUnit = TimeUnit.MILLISECONDS)
	public void updateStock(Long id, Long quantity) {
		var product = productRepository.findByIdOrThrow(id);
		product.updateStock(quantity);
	}

	@Transactional
	public void deleteProduct(Long id) {
		var product = productRepository.findByIdOrThrow(id);
		product.delete();
	}
}
