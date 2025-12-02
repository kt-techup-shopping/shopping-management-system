package com.shop.domain.category.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.category.repository.CategoryRepository;
import com.shop.domain.category.response.AdminCategoryListResponse;
import com.shop.domain.category.response.AdminCategoryResponse;
import com.shop.domain.category.response.CategoryDetailResponse;
import com.shop.domain.category.model.Category;
import com.shop.global.common.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	// 상위 카테고리 리스트 포함하여 반환
	public List<CategoryDetailResponse> getCategoryHierarchy(Category category) {
		List<Category> list = new ArrayList<>();
		while (category != null) {
			list.add(category);
			category = category.getParent();
		}

		Collections.reverse(list);
		return list.stream()
			.map(c -> new CategoryDetailResponse(c.getId(), c.getName()))
			.toList();
	}

	// 관리자 카테고리 등록
	@Transactional
	public void createCategory(String name, Long parentCategoryId) {
		var parentCategory = categoryRepository.findById(parentCategoryId).orElse(null);
		categoryRepository.save(
			new Category(
				name,
				parentCategory
			)
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
