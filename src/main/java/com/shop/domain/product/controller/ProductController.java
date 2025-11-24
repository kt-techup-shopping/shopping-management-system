package com.shop.domain.product.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.product.dto.response.ProductSearchResponse;
import com.shop.domain.product.service.ProductService;
import com.shop.global.common.ApiResult;
import com.shop.global.common.Paging;
import com.shop.global.common.SwaggerAssistance;
import com.shop.domain.product.dto.request.ProductRequest;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Product")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController extends SwaggerAssistance {

	private final ProductService productService;

	@GetMapping
	public ApiResult<Page<ProductSearchResponse>> search(
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) Long categoryId,
		@RequestParam(required = false) Boolean activeOnly,
		@Parameter Paging paging
	) {
		return ApiResult.ok(productService.search(keyword, categoryId, activeOnly, paging.sort(), paging.toPageable()));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResult<Void> create(@RequestBody @Valid ProductRequest.Create request) {
		productService.create(
			request.getName(),
			request.getPrice(),
			request.getQuantity()
		);

		return ApiResult.ok();
	}

	@PutMapping("/{id}")
	public ApiResult<Void> update(
		@PathVariable Long id,
		@RequestBody @Valid ProductRequest.Update request
	) {
		productService.update(
			id,
			request.getName(),
			request.getPrice(),
			request.getQuantity()
		);

		return ApiResult.ok();
	}

	@PatchMapping("/{id}/sold-out")
	public void soldOut(@PathVariable Long id) {
		productService.soldOut(id);
	}

	@PatchMapping("/{id}/activate")
	public ApiResult<Void> activate(@PathVariable Long id) {
		productService.activate(id);

		return ApiResult.ok();
	}

	@PatchMapping("/{id}/in-activate")
	public ApiResult<Void> inActivate(@PathVariable Long id) {
		productService.inActivate(id);

		return ApiResult.ok();
	}

	@DeleteMapping("/{id}")
	public ApiResult<Void> remove(@PathVariable Long id) {
		productService.delete(id);

		return ApiResult.ok();
	}
}
