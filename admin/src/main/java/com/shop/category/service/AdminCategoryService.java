package com.shop.category.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.ErrorCode;
import com.shop.category.response.AdminCategoryDetailResponse;
import com.shop.category.response.AdminCategoryListResponse;
import com.shop.category.response.AdminCategoryResponse;
import com.shop.domain.category.Category;
import com.shop.repository.category.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminCategoryService {

	private final CategoryRepository categoryRepository;

	// 상위 카테고리 리스트 포함하여 반환
	public List<AdminCategoryDetailResponse> getCategoryHierarchy(Category category) {
		return category.getHierarchy().stream()
			.map(c -> new AdminCategoryDetailResponse(c.getId(), c.getName()))
			.toList();
	}

	// 관리자 카테고리 등록
	@Transactional
	public AdminCategoryDetailResponse createCategory(String name, Long parentCategoryId) {
		var parentCategory = Optional.ofNullable(parentCategoryId)
			.map(id -> categoryRepository.findByIdOrThrow(id, ErrorCode.NOT_FOUND_CATEGORY))
			.orElse(null);

		var category = categoryRepository.save(
			new Category(
				name,
				parentCategory
			)
		);
		return new AdminCategoryDetailResponse(
			category.getId(),
			category.getName()
		);
	}

	// 관리자 카테고리 목록 조회
	public AdminCategoryListResponse getCategories() {
		// 1) 최상위 카테고리만 조회 (parent가 null인 애들)
		List<Category> roots = categoryRepository.findByParentIsNull();

		// 2) 트리 구조로 DTO 변환
		List<AdminCategoryResponse> rootDtos = roots.stream()
			.map(this::buildCategoryTree)
			.toList();

		return new AdminCategoryListResponse(rootDtos);
	}

	private AdminCategoryResponse buildCategoryTree(Category category) {
		return new AdminCategoryResponse(
			category.getId(),
			category.getName(),
			category.getParent() != null ? category.getParent().getId() : null,
			category.getChildren().stream()
				.map(this::buildCategoryTree)
				.toList()
		);
	}
}
