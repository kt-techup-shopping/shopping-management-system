package com.shop.product.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
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

import com.shop.ApiResult;
import com.shop.ErrorCode;
import com.shop.Paging;
import com.shop.docs.ApiErrorCodeExample;
import com.shop.docs.ApiErrorCodeExamples;
import com.shop.product.request.AdminProductSoldOutRequest;
import com.shop.product.request.AdminProductCreateRequest;
import com.shop.product.request.AdminProductUpdateRequest;
import com.shop.product.response.AdminProductDetailResponse;
import com.shop.product.response.AdminProductInfoResponse;
import com.shop.product.response.AdminProductSearchResponse;
import com.shop.product.response.AdminProductStatusResponse;
import com.shop.product.response.AdminProductStockResponse;
import com.shop.product.service.AdminProductService;

import io.swagger.v3.oas.annotations.Operation;
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
	public ApiResult<AdminProductInfoResponse> create(@RequestBody @Valid AdminProductCreateRequest request) {
		var product = adminProductService.create(
			request.name(),
			request.price(),
			request.stock(),
			request.description(),
			request.color(),
			request.categoryId()
		);

		return ApiResult.ok(product);
	}

	@Operation(summary = "상품 목록 조회", description = "관리자가 상품 목록을 조회하며 필터, 정렬, 페이징을 지원합니다.")
	@ApiErrorCodeExamples(ErrorCode.INVALID_SORT_OPTION)
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Page<AdminProductSearchResponse>> getAdminSearchList(
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) Long categoryId,
		@RequestParam(required = false) Boolean activeOnly,
		@RequestParam(required = false) String sort,
		@ParameterObject Paging paging
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
	@ApiErrorCodeExamples({
		ErrorCode.NOT_FOUND_PRODUCT,
		ErrorCode.NOT_FOUND_CATEGORY
	})
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<AdminProductDetailResponse> getAdminDetailById(@PathVariable Long id) {
		return ApiResult.ok(adminProductService.getAdminDetailById(id));
	}

	@Operation(summary = "상품 정보 수정", description = "관리자가 상품의 상세 정보를 수정합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_CATEGORY)
	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<AdminProductInfoResponse> updateDetail(
		@PathVariable Long id,
		@RequestBody @Valid AdminProductUpdateRequest request
	) {
		var product = adminProductService.updateDetail(
			id,
			request.name(),
			request.price(),
			request.description(),
			request.color(),
			request.quantity(),
			request.status(),
			request.categoryId()
		);
		return ApiResult.ok(product);
	}

	@Operation(summary = "상품 활성화", description = "관리자가 상품을 활성 상태로 변경합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_PRODUCT)
	@PutMapping("/{id}/activate")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<AdminProductStatusResponse> updateActivated(@PathVariable Long id) {
		return ApiResult.ok(adminProductService.updateActivated(id));
	}

	@Operation(summary = "상품 비활성화", description = "관리자가 상품을 비활성 상태로 변경합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_PRODUCT)
	@PutMapping("/{id}/in-activate")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<AdminProductStatusResponse> updateInActivated(@PathVariable Long id) {
		return ApiResult.ok(adminProductService.updateInActivated(id));
	}

	@Operation(summary = "상품 품절 토글", description = "관리자가 상품의 품절 상태를 토글합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_PRODUCT)
	@PutMapping("/{id}/toggle-sold-out")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<AdminProductStatusResponse> updateSoldOut(@PathVariable Long id) {
		return ApiResult.ok(adminProductService.updateSoldOutToggle(id));
	}

	@Operation(summary = "상품 다중 품절 처리", description = "관리자가 선택한 상품들을 한 번에 품절 처리합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_PRODUCT)
	@PutMapping("/sold-out")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<List<AdminProductStatusResponse>> updateSoldOutList(
		@RequestBody AdminProductSoldOutRequest request) {
		return ApiResult.ok(adminProductService.updateSoldOutList(request.productIds()));
	}

	@Operation(summary = "상품 재고 목록 조회", description = "관리자가 상품 재고를 조회하며, 이름/ID로 검색 가능하고 페이징 지원.")
	@GetMapping("/stock")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Page<AdminProductStockResponse>> getStockDetailList(
		@RequestParam(required = false) String keyword,
		@ParameterObject Paging paging
	) {
		return ApiResult.ok(adminProductService.getStockList(keyword, paging.toPageable()));
	}

	@Operation(summary = "상품 재고 수정", description = "관리자가 특정 상품의 재고 수량을 변경합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_PRODUCT)
	@PutMapping("/{id}/stock/{quantity}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<AdminProductInfoResponse> updateStock(
		@PathVariable Long id,
		@PathVariable Long quantity
	) {
		return ApiResult.ok(adminProductService.updateStock(id, quantity));
	}

	@Operation(summary = "상품 삭제", description = "관리자가 상품을 삭제 처리합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_PRODUCT)
	@PutMapping("/{id}/delete")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<AdminProductStatusResponse> deleteProduct(@PathVariable Long id) {
		return ApiResult.ok(adminProductService.deleteProduct(id));
	}
}

