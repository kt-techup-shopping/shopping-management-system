package com.shop.domain.category;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shop.domain.category.model.Category;
import com.shop.domain.category.repository.CategoryRepository;
import com.shop.domain.category.service.CategoryService;
import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;

@ExtendWith(MockitoExtension.class)
class CategoryTest {

	@Mock
	CategoryRepository categoryRepository;

	@InjectMocks
	CategoryService categoryService;

	@Test
	void 카테고리_생성_성공() {
		// given
		var name = "상의";
		given(categoryRepository.save(any(Category.class)))
			.willReturn(new Category(name, null));

		// when
		var response = categoryService.createCategory(name, null);

		// then
		assertThat(response.name()).isEqualTo(name);
	}

	@Test
	void 카테고리_생성_실패__존재하지_않는_부모ID() {
		// given
		var invalidParentId = 999L;
		given(categoryRepository.findByIdOrThrow(invalidParentId, ErrorCode.NOT_FOUND_CATEGORY))
			.willThrow(new CustomException(ErrorCode.NOT_FOUND_CATEGORY));

		// when & then
		assertThrowsExactly(CustomException.class,
			() -> categoryService.createCategory("상의", invalidParentId));
	}
}
