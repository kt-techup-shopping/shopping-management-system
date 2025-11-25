package com.shop.domain.category.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.shop.domain.category.model.Category;
import com.shop.domain.category.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	public List<Long> getCategoryAndChildren(Long categoryId) {

		List<Long> ids = new ArrayList<>();
		ids.add(categoryId);
		ids.addAll(
			categoryRepository.findByParentId(categoryId)
				.stream()
				.map(Category::getId)
				.toList()
		);

		return ids;
	}

}
