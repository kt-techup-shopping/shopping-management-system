package com.shop.domain.category;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import com.shop.domain.category.model.Category;
import com.shop.global.common.CustomException;

public class CategoryTest {

	@Test
	void 부모_카테고리_생성_성공() {
		var category = new Category(
			"상의",
			null
		);

		assertThat(category.getName()).isEqualTo("상의");
		assertThat(category.getParent()).isEqualTo(null);
	}

	@Test
	void 자식_카테고리_생성_성공() {
		var parentCategory = new Category(
			"상의",
			null
		);

		var category = new Category(
			"티셔츠",
			parentCategory
		);
		assertThat(category.getName()).isEqualTo("티셔츠");
		assertThat(category.getParent()).isEqualTo(parentCategory);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 부모_카테고리_생성_실패__카테고리명_null_이거나_공백(String name) {
		assertThrowsExactly(CustomException.class, () -> new Category(
			name,
			null
		));
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 자식_카테고리_생성_실패__카테고리명_null_이거나_공백(String name) {
		var parentCategory = new Category(
			"상의",
			null
		);

		assertThrowsExactly(CustomException.class, () -> new Category(
			name,
			parentCategory
		));
	}

}