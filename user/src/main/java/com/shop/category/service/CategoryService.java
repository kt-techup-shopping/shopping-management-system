package com.shop.category.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shop.category.response.CategoryDetailResponse;
import com.shop.domain.category.Category;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	// 상위 카테고리 리스트 포함하여 반환
	public List<CategoryDetailResponse> getCategoryHierarchy(Category category) {
		return category.getHierarchy().stream()
			.map(c -> new CategoryDetailResponse(c.getId(), c.getName()))
			.toList();
	}

}
