package com.shop.service.category;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

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
class CategoryServiceTest {

	@Mock
	CategoryRepository categoryRepository;

	@InjectMocks
	CategoryService categoryService;

	@Test
	void 부모_카테고리_생성_성공() {
		// given
		var name = "상의";
		given(categoryRepository.save(any(Category.class)))
			.willReturn(new Category(name, null));

		// when
		var category = categoryService.createCategory(name, null);

		// then
		assertThat(category.name()).isEqualTo("상의");
	}

	@Test
	void 자식_카테고리_생성_실패__존재하지_않는_부모_카테고리_ID() {
		// given
		var invalidParentId = 999L;

		given(categoryRepository.findByIdOrThrow(invalidParentId, ErrorCode.NOT_FOUND_CATEGORY))
			.willThrow(new CustomException(ErrorCode.NOT_FOUND_CATEGORY));

		// when & then
		assertThrowsExactly(CustomException.class,
			() -> categoryService.createCategory("티셔츠", invalidParentId));
	}

	@Test
	void 카테고리_트리_조회_성공() {
		// given
		var root = new Category("상의", null);
		var firstChild = new Category("티셔츠", root);
		var secondChild = new Category("니트", root);

		root.getChildren().add(firstChild);
		root.getChildren().add(secondChild);

		given(categoryRepository.findByParentIsNull())
			.willReturn(List.of(root));

		// when
		var response = categoryService.getCategories();

		// then
		assertThat(response.categories()).hasSize(1);
		assertThat(response.categories().getFirst().name()).isEqualTo("상의");

		var child = response.categories().getFirst().children().getFirst();
		assertThat(child.name()).isEqualTo("티셔츠");
	}

	@Test
	void 카테고리_트리_조회_성공__빈_목록() {
		// given
		given(categoryRepository.findByParentIsNull()).willReturn(List.of());

		// when
		var response = categoryService.getCategories();

		// then
		assertThat(response.categories()).isEmpty();
	}
}
