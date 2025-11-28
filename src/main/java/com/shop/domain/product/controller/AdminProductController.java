package com.shop.domain.product.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.product.request.ProductCreateRequest;
import com.shop.domain.product.request.ProductUpdateRequest;
import com.shop.domain.product.response.AdminProductDetailResponse;
import com.shop.domain.product.response.AdminProductSearchResponse;
import com.shop.domain.product.service.AdminProductService;
import com.shop.global.common.ApiResult;
import com.shop.global.common.Paging;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/products")
public class AdminProductController {

	private final AdminProductService adminProductService;

	// 관리자 상품 등록
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResult<Void> create(@RequestBody @Valid ProductCreateRequest request) {
		adminProductService.create(
			request.name(),
			request.price(),
			request.stock(),
			request.description(),
			request.color(),
			request.categoryId()
		);
		return ApiResult.ok();
	}

	// 관리자 상품 목록 조회
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Page<AdminProductSearchResponse>> getAdminSearchList(
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) Long categoryId,
		@RequestParam(required = false) Boolean activeOnly,
		@RequestParam(required = false) String productSort,
		@Parameter Paging paging
	) {
		return ApiResult.ok(adminProductService.getAdminSearchList(keyword, categoryId, activeOnly, productSort, paging.toPageable()));
	}

	// 관리자 상품 상세 조회
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<AdminProductDetailResponse> getAdminDetailById(@PathVariable Long id) {
		return ApiResult.ok(adminProductService.getAdminDetailById(id));
	}

	// 관리자 상품 정보 수정
	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> updateDetail(
		@PathVariable Long id,
		@RequestBody @Valid ProductUpdateRequest request
	) {
		adminProductService.updateDetail(
			id,
			request.name(),
			request.price(),
			request.description(),
			request.color(),
			request.deltaStock(),
			request.status(),
			request.categoryId()
		);
		return ApiResult.ok();
	}
}
