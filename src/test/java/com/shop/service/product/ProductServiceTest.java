package com.shop.service.product;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.shop.domain.category.model.Category;
import com.shop.domain.category.response.CategoryDetailResponse;
import com.shop.domain.category.service.CategoryService;
import com.shop.domain.discount.model.DiscountType;
import com.shop.domain.product.repository.ProductRepository;
import com.shop.domain.product.request.ProductSort;
import com.shop.domain.product.response.ProductDetailQueryResponse;
import com.shop.domain.product.response.ProductSearchResponse;
import com.shop.domain.product.service.ProductService;
import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private CategoryService categoryService;

	@InjectMocks
	private ProductService productService;

	@Test
	void 상품_목록_조회_성공() {
		// given
		// 검색 조건
		var keyword = "반팔";
		var categoryId = 1L;
		var activeOnly = true;
		var sort = "latest";
		var pageable = PageRequest.of(0, 10);

		// 검색 결과 상품
		var product = mock(ProductSearchResponse.class);
		var expectedPage = new PageImpl<>(List.of(product), pageable, 1);

		// 검색 결과 페이지
		given(productRepository.getSearchList(
			keyword,
			categoryId,
			activeOnly,
			ProductSort.from(sort),
			pageable
		)).willReturn(expectedPage);

		// when
		var result = productService.getSearchList(keyword, categoryId, activeOnly, sort, pageable);

		// then
		assertThat(result).isSameAs(expectedPage);
		then(productRepository).should()
			.getSearchList(keyword, categoryId, activeOnly, ProductSort.from(sort), pageable);
	}

	@Test
	void 상품_상세_조회_실패__상품_없으면_예외() {
		// given
		// 상품 존재 X
		var productId = 1L;
		given(productRepository.existsById(productId)).willReturn(false);

		// when & then
		var exception = assertThrowsExactly(CustomException.class,
			() -> productService.getDetailById(productId)
		);

		assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_PRODUCT);
		then(productRepository).should().existsById(productId);
		then(productRepository).should(never()).findDetailById(anyLong());
	}

	@Test
	void 상품_상세_조회_실패__카테고리가_null이면_예외() {
		// given
		// 상품 존재
		var productId = 1L;
		given(productRepository.existsById(productId)).willReturn(true);

		// 카테고리 존재 X 상품
		var projection = mock(ProductDetailQueryResponse.class);
		given(projection.category()).willReturn(null);
		given(productRepository.findDetailById(productId)).willReturn(projection);

		// when & then
		var ex = assertThrowsExactly(CustomException.class,
			() -> productService.getDetailById(productId));

		assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_CATEGORY);
		then(productRepository).should().existsById(productId);
		then(productRepository).should().findDetailById(productId);
		then(categoryService).shouldHaveNoInteractions();
	}

	@Test
	void 상품_상세_조회_성공() {
		// given
		// 상품 존재
		var productId = 1L;
		given(productRepository.existsById(productId)).willReturn(true);

		// 상품 상세 정보
		var category = new Category("상의", null);
		var projection = mock(ProductDetailQueryResponse.class);

		given(projection.id()).willReturn(productId);
		given(projection.name()).willReturn("반팔티");
		given(projection.price()).willReturn(15000L);
		given(projection.description()).willReturn("베이직 반팔티");
		given(projection.color()).willReturn("WHITE");
		given(projection.status()).willReturn(com.shop.domain.product.model.ProductStatus.ACTIVATED);
		given(projection.discountValue()).willReturn(1000L);
		given(projection.discountType()).willReturn(DiscountType.FIXED);
		given(projection.discountedPrice()).willReturn(14000L);
		given(projection.category()).willReturn(category);
		given(productRepository.findDetailById(productId)).willReturn(projection);

		// 상품 카테고리
		var categories = List.of(mock(CategoryDetailResponse.class));
		given(categoryService.getCategoryHierarchy(category)).willReturn(categories);

		// when
		var result = productService.getDetailById(productId);

		// then
		assertThat(result.id()).isEqualTo(productId);
		assertThat(result.name()).isEqualTo("반팔티");
		assertThat(result.price()).isEqualTo(15000L);
		assertThat(result.description()).isEqualTo("베이직 반팔티");
		assertThat(result.color()).isEqualTo("WHITE");
		assertThat(result.status()).isEqualTo(com.shop.domain.product.model.ProductStatus.ACTIVATED);
		assertThat(result.discountValue()).isEqualTo(1000L);
		assertThat(result.discountType()).isEqualTo(DiscountType.FIXED);
		assertThat(result.discountedPrice()).isEqualTo(14000L);
		assertThat(result.categories()).isEqualTo(categories);

		then(productRepository).should().existsById(productId);
		then(productRepository).should().findDetailById(productId);
		then(categoryService).should().getCategoryHierarchy(category);
	}
}
