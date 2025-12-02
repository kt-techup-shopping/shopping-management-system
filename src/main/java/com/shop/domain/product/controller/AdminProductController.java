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
import com.shop.domain.product.request.ProductSoldOutRequest;
import com.shop.domain.product.request.ProductUpdateRequest;
import com.shop.domain.product.response.AdminProductDetailResponse;
import com.shop.domain.product.response.AdminProductSearchResponse;
import com.shop.domain.product.response.AdminProductStockResponse;
import com.shop.domain.product.service.AdminProductService;
import com.shop.global.common.ApiResult;
import com.shop.global.common.ErrorCode;
import com.shop.global.common.Paging;
import com.shop.global.docs.ApiErrorCodeExample;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "관리자 상품", description = "관리자용 상품 관리 API")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/products")
public class AdminProductController {

	private final AdminProductService adminProductService;

	@Operation(summary = "상품 등록", description = "관리자가 새로운 상품을 등록합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_CATEGORY)
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

	@Operation(summary = "상품 목록 조회", description = "관리자가 상품 목록을 조회하며 필터, 정렬, 페이징을 지원합니다.")
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Page<AdminProductSearchResponse>> getAdminSearchList(
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) Long categoryId,
		@RequestParam(required = false) Boolean activeOnly,
		@RequestParam(required = false) String sort,
		@Parameter Paging paging
	) {
		return ApiResult.ok(
			adminProductService.getAdminSearchList(
				keyword,
				categoryId,
				activeOnly,
				sort,
				paging.toPageable()
			)
		);
	}

	@Operation(summary = "상품 상세 조회", description = "관리자가 상품 ID로 상세 정보를 조회합니다.")
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<AdminProductDetailResponse> getAdminDetailById(@PathVariable Long id) {
		return ApiResult.ok(adminProductService.getAdminDetailById(id));
	}

	@Operation(summary = "상품 정보 수정", description = "관리자가 상품의 상세 정보를 수정합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_CATEGORY)
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
			request.quantity(),
			request.status(),
			request.categoryId()
		);
		return ApiResult.ok();
	}

	@Operation(summary = "상품 활성화", description = "관리자가 상품을 활성 상태로 변경합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_PRODUCT)
	@PutMapping("/{id}/activate")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> updateActivated(@PathVariable Long id) {
		adminProductService.updateActivated(id);
		return ApiResult.ok();
	}

	@Operation(summary = "상품 비활성화", description = "관리자가 상품을 비활성 상태로 변경합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_PRODUCT)
	@PutMapping("/{id}/in-activate")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> updateInActivated(@PathVariable Long id) {
		adminProductService.updateInActivated(id);
		return ApiResult.ok();
	}

	@Operation(summary = "상품 품절 토글", description = "관리자가 상품의 품절 상태를 토글합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_PRODUCT)
	@PutMapping("/{id}/toggle-sold-out")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> updateSoldOut(@PathVariable Long id) {
		adminProductService.updateSoldOutToggle(id);
		return ApiResult.ok();
	}

	@Operation(summary = "상품 다중 품절 처리", description = "관리자가 선택한 상품들을 한 번에 품절 처리합니다.")
	@PutMapping("/sold-out")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> updateSoldOutList(@RequestBody ProductSoldOutRequest request) {
		adminProductService.updateSoldOutList(request.productIds());
		return ApiResult.ok();
	}

	@Operation(summary = "상품 재고 목록 조회", description = "관리자가 상품 재고를 조회하며, 이름/ID로 검색 가능하고 페이징 지원.")
	@GetMapping("/stock")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Page<AdminProductStockResponse>> getStockDetailList(
		@RequestParam(required = false) String keyword,
		@Parameter Paging paging
	) {
		return ApiResult.ok(adminProductService.getStockList(keyword, paging.toPageable()));
	}

	@Operation(summary = "상품 재고 수정", description = "관리자가 특정 상품의 재고 수량을 변경합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_PRODUCT)
	@PutMapping("/{id}/stock/{quantity}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> updateStock(
		@PathVariable Long id,
		@PathVariable Long quantity
	) {
		adminProductService.updateStock(id, quantity);
		return ApiResult.ok();
	}

	@Operation(summary = "상품 삭제", description = "관리자가 상품을 삭제 처리합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_PRODUCT)
	@PutMapping("/{id}/delete")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> deleteProduct(@PathVariable Long id) {
		adminProductService.deleteProduct(id);
		return ApiResult.ok();
	}
}

