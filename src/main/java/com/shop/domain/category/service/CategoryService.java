package com.shop.domain.category.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.shop.domain.category.response.CategoryDetailResponse;
import com.shop.domain.category.model.Category;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

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

}
