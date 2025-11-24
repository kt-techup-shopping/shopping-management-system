package com.shop.domain.category.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.shop.domain.category.dto.CategoryDetailResponse;
import com.shop.domain.category.model.Category;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

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
