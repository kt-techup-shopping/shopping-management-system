package com.shop.product.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.ErrorCode;
import com.shop.Lock;
import com.shop.Preconditions;
import com.shop.category.service.AdminCategoryService;
import com.shop.domain.product.Product;
import com.shop.domain.product.ProductSort;
import com.shop.domain.product.ProductStatus;
import com.shop.product.response.AdminProductDetailResponse;
import com.shop.product.response.AdminProductInfoResponse;
import com.shop.product.response.AdminProductSearchResponse;
import com.shop.product.response.AdminProductStatusResponse;
import com.shop.product.response.AdminProductStockResponse;
import com.shop.repository.category.CategoryRepository;
import com.shop.repository.product.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final AdminCategoryService adminCategoryService;

	// 관리자 상품 등록
	@Transactional
	public AdminProductInfoResponse create(String name, Long price, Long stock, String description, String color,
		Long categoryId) {
		var category = categoryRepository.findByIdOrThrow(categoryId, ErrorCode.NOT_FOUND_CATEGORY);
		var product = productRepository.save(new Product(
			name,
			price,
			stock,
			description,
			color,
			category
		));
		return new AdminProductInfoResponse(
			product.getId(),
			product.getName(),
			product.getPrice(),
			product.getDescription(),
			product.getStock()
		);
	}

	// 관리자 상품 목록 조회
	public Page<AdminProductSearchResponse> getAdminSearchList(String keyword, Long categoryId, Boolean activeOnly,
		String sort, PageRequest pageable) {
		var searchResult =  productRepository.getAdminSearchList(keyword, categoryId, activeOnly, ProductSort.from(sort), pageable);
		return searchResult.map(product -> new AdminProductSearchResponse(
			product.id(),
			product.name(),
			product.price(),
			product.stock(),
			product.status(),
			product.discountValue(),
			product.discountType(),
			product.discountedPrice()
		));

	}

	// 관리자 상품 상세 조회
	public AdminProductDetailResponse getAdminDetailById(Long id) {
		var isExisted = productRepository.existsById(id);
		Preconditions.validate(isExisted, ErrorCode.NOT_FOUND_PRODUCT);
		var product = productRepository.findAdminDetailById(id);
		var categoryList = adminCategoryService.getCategoryHierarchy(product.category());

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
	@Lock(key = Lock.Key.PRODUCT, index = 0, waitTime = 1500, leaseTime = 1000, timeUnit = TimeUnit.MILLISECONDS)
	public AdminProductInfoResponse updateDetail(Long id, String name, Long price, String description, String color,
		Long quantity,
		String status, Long categoryId) {
		var product = productRepository.findByIdOrThrow(id);
		var category = categoryRepository.findByIdOrThrow(categoryId, ErrorCode.NOT_FOUND_CATEGORY);

		product.update(
			name,
			price,
			description,
			color,
			quantity,
			ProductStatus.from(status),
			category
		);
		return new AdminProductInfoResponse(
			product.getId(),
			product.getName(),
			product.getPrice(),
			product.getDescription(),
			product.getStock()
		);
	}

	// 관리자 상품 상태 비활성화
	@Transactional
	public AdminProductStatusResponse updateActivated(Long id) {
		var product = productRepository.findByIdOrThrow(id);
		product.activate();
		return new AdminProductStatusResponse(
			product.getId(),
			product.getStatus().name()
		);
	}

	// 관리자 상품 상태 비활성화
	@Transactional
	public AdminProductStatusResponse updateInActivated(Long id) {
		var product = productRepository.findByIdOrThrow(id);
		product.inActivate();
		return new AdminProductStatusResponse(
			product.getId(),
			product.getStatus().name()
		);
	}

	// 관리자 상품 상태 품절 토글
	@Transactional
	public AdminProductStatusResponse updateSoldOutToggle(Long id) {
		var product = productRepository.findByIdOrThrow(id);
		product.toggleSoldOut();
		return new AdminProductStatusResponse(
			product.getId(),
			product.getStatus().name()
		);
	}

	// 관리자 상품 상태 다중 품절
	@Transactional
	public List<AdminProductStatusResponse> updateSoldOutList(List<Long> ids) {
		// 상품 리스트 유효성 검사
		var products = productRepository.findAllById(ids);
		Preconditions.validate(products.size() == ids.size(), ErrorCode.NOT_FOUND_PRODUCT);
		// 품절 업데이트
		products.forEach(Product::soldOut);

		return products.stream()
			.map(product -> new AdminProductStatusResponse(
				product.getId(),
				product.getStatus().name()
			))
			.toList();
	}

	// 관리자 상품 재고 목록 조회
	public Page<AdminProductStockResponse> getStockList(String keyword, PageRequest paging) {
		 return productRepository.getStockList(keyword, paging)
			 .map(product -> new AdminProductStockResponse(
				 product.id(),
				 product.name(),
				 product.availableStock(),
				 product.reservedStock(),
				 product.totalStock()
			 ));
	}

	// 관리자 상품 재고 수정
	@Lock(key = Lock.Key.PRODUCT, index = 0, waitTime = 1000, leaseTime = 500, timeUnit = TimeUnit.MILLISECONDS)
	public AdminProductInfoResponse updateStock(Long id, Long quantity) {
		var product = productRepository.findByIdOrThrow(id);
		product.updateStock(quantity);
		return new AdminProductInfoResponse(
			product.getId(),
			product.getName(),
			product.getPrice(),
			product.getDescription(),
			product.getStock()
		);
	}

	@Transactional
	public AdminProductStatusResponse deleteProduct(Long id) {
		var product = productRepository.findByIdOrThrow(id);
		product.delete();
		return new AdminProductStatusResponse(
			product.getId(),
			product.getStatus().name()
		);
	}
}
