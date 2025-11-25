package com.shop.domain.product.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.product.request.ProductCreateRequest;
import com.shop.domain.product.service.AdminProductService;
import com.shop.global.common.ApiResult;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
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
}
