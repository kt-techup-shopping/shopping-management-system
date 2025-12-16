package com.shop.product.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.shop.ErrorCode;
import com.shop.Preconditions;
import com.shop.category.service.CategoryService;
import com.shop.domain.product.ProductSort;
import com.shop.product.response.ProductDetailResponse;
import com.shop.product.response.ProductSearchResponse;
import com.shop.repository.product.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
	private final ProductRepository productRepository;
	private final CategoryService categoryService;

	// 상품 목록 조회
	public Page<ProductSearchResponse> getSearchList(String keyword, Long categoryId, Boolean activeOnly, String sort,
		PageRequest pageable) {
		return productRepository.getSearchList(keyword, categoryId, activeOnly, ProductSort.from(sort), pageable);
	}

	// 상품 상세 조회
	public ProductDetailResponse getDetailById(Long id) {
		var	existedProduct = productRepository.existsById(id);
		Preconditions.validate(existedProduct, ErrorCode.NOT_FOUND_PRODUCT);
		var product = productRepository.findDetailById(id);

		Preconditions.validate(product.category() != null, ErrorCode.NOT_FOUND_CATEGORY);
		var categoryList = categoryService.getCategoryHierarchy(product.category());

		// ProductDetailProjection + CategoryList
		return new ProductDetailResponse(
			product.id(),
			product.name(),
			product.price(),
			product.description(),
			product.color(),
			product.status(),
			product.discountValue(),
			product.discountType(),
			product.discountedPrice(),
			categoryList
		);
	}
}
