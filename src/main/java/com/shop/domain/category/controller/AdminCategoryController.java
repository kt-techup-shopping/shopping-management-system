package com.shop.domain.category.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.category.request.AdminCategoryCreateRequest;
import com.shop.domain.category.service.CategoryService;
import com.shop.global.common.ApiResult;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/categories")
@Tag(name = "카테고리 관리자 API")
public class AdminCategoryController {

	private final CategoryService categoryService;

	// 관리자 카테고리 등록
	@PostMapping
	public ApiResult<Void> createCategory(AdminCategoryCreateRequest request) {
		categoryService.createCategory(request.name(), request.parentCategoryId());
		return ApiResult.ok();
	}
}
