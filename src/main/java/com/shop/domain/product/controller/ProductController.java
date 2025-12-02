package com.shop.domain.product.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.product.request.ProductRequest;
import com.shop.domain.product.response.ProductDetailResponse;
import com.shop.domain.product.response.ProductSearchResponse;
import com.shop.domain.product.service.ProductService;
import com.shop.global.common.ApiResult;
import com.shop.global.common.ErrorCode;
import com.shop.global.common.Paging;
import com.shop.global.common.SwaggerAssistance;
import com.shop.global.docs.ApiErrorCodeExample;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "상품", description = "상품을 관리하는 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController{

	private final ProductService productService;

	@Operation(summary = "상품 검색/목록 조회", description = "상품을 검색하거나 목록을 조회합니다. 필터링, 정렬, 페이징 지원.")
	@SecurityRequirements(value = {})
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Page<ProductSearchResponse>> getSearchList(
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) Long categoryId,
		@RequestParam(required = false) Boolean activeOnly,
		@RequestParam(required = false) String sort,
		@Parameter Paging paging
	) {
		return ApiResult.ok(productService.getSearchList(keyword, categoryId, activeOnly, sort, paging.toPageable()));
	}

	@Operation(summary = "상품 상세 조회", description = "상품 ID를 통해 상세 정보를 조회합니다.")
	@SecurityRequirements(value = {})
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<ProductDetailResponse> getDetailById(@PathVariable Long id){
		return ApiResult.ok(productService.getDetailById(id));
	}

	@Operation(summary = "상품 품절 처리", description = "상품을 품절 상태로 변경합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_PRODUCT)
	@PatchMapping("/{id}/sold-out")
	public void soldOut(@PathVariable Long id) {
		productService.soldOut(id);
	}

	@Operation(summary = "상품 활성화", description = "상품을 활성 상태로 변경합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_PRODUCT)
	@PatchMapping("/{id}/activate")
	public ApiResult<Void> activate(@PathVariable Long id) {
		productService.activate(id);
		return ApiResult.ok();
	}

	@Operation(summary = "상품 비활성화", description = "상품을 비활성 상태로 변경합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_PRODUCT)
	@PatchMapping("/{id}/in-activate")
	public ApiResult<Void> inActivate(@PathVariable Long id) {
		productService.inActivate(id);
		return ApiResult.ok();
	}

	@Operation(summary = "상품 삭제", description = "상품을 데이터베이스에서 삭제합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_PRODUCT)
	@DeleteMapping("/{id}")
	public ApiResult<Void> remove(@PathVariable Long id) {
		productService.delete(id);
		return ApiResult.ok();
	}
}

