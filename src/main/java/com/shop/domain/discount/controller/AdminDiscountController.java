package com.shop.domain.discount.controller;

import static org.springframework.data.jpa.domain.AbstractPersistable_.*;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.discount.request.AdminDiscountCreateRequest;
import com.shop.domain.discount.service.DiscountService;
import com.shop.global.common.ApiResult;
import com.shop.global.common.ErrorCode;
import com.shop.global.docs.ApiErrorCodeExample;
import com.shop.global.docs.ApiErrorCodeExamples;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "관리자 할인", description = "관리자 할인 API")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/discounts")
public class AdminDiscountController {

	private final DiscountService adminProductService;

	@Operation(summary = "관리자 상품 할인 등록")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_PRODUCT)
	@PostMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> createDiscount(@RequestBody @Valid AdminDiscountCreateRequest request) {
		adminProductService.createDiscount(
			request.productId(),
			request.value(),
			request.type()
		);
		return ApiResult.ok();
	}
}
