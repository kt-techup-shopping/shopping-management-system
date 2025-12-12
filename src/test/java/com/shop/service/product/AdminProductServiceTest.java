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
import com.shop.domain.category.repository.CategoryRepository;
import com.shop.domain.category.response.CategoryDetailResponse;
import com.shop.domain.category.service.CategoryService;
import com.shop.domain.discount.model.DiscountType;
import com.shop.domain.product.model.Product;
import com.shop.domain.product.model.ProductStatus;
import com.shop.domain.product.repository.ProductRepository;
import com.shop.domain.product.request.ProductSort;
import com.shop.domain.product.response.AdminProductDetailQueryResponse;
import com.shop.domain.product.response.AdminProductSearchResponse;
import com.shop.domain.product.response.AdminProductStockResponse;
import com.shop.domain.product.service.AdminProductService;
import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;

@ExtendWith(MockitoExtension.class)
class AdminProductServiceTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private CategoryService categoryService;

	@InjectMocks
	private AdminProductService adminProductService;

	@Test
	void 관리자_상품_등록_성공() {
		// given
		var name = "반팔티";
		var price = 15000L;
		var stock = 10L;
		var description = "설명";
		var color = "WHITE";
		var categoryId = 1L;

		// 카테고리 조회
		var category = mock(Category.class);
		given(categoryRepository.findByIdOrThrow(categoryId, ErrorCode.NOT_FOUND_CATEGORY))
			.willReturn(category);

		// 상품 저장 결과
		var saved = mock(Product.class);
		given(saved.getId()).willReturn(1L);
		given(saved.getName()).willReturn(name);
		given(saved.getPrice()).willReturn(price);
		given(saved.getDescription()).willReturn(description);
		given(saved.getStock()).willReturn(stock);
		given(productRepository.save(any(Product.class))).willReturn(saved);

		// when
		var result = adminProductService.create(name, price, stock, description, color, categoryId);

		// then
		assertThat(result.id()).isEqualTo(1L);
		assertThat(result.name()).isEqualTo(name);
		assertThat(result.price()).isEqualTo(price);
		assertThat(result.description()).isEqualTo(description);
		assertThat(result.stock()).isEqualTo(stock);

		then(categoryRepository).should().findByIdOrThrow(categoryId, ErrorCode.NOT_FOUND_CATEGORY);
		then(productRepository).should().save(any(Product.class));
	}

	@Test
	void 관리자_상품_목록_조회_성공() {
		// given
		// 검색 조건
		var keyword = "반팔";
		var categoryId = 1L;
		var activeOnly = true;
		var sort = "latest";
		var pageable = PageRequest.of(0, 10);

		// 검색 결과 페이지
		var item = mock(AdminProductSearchResponse.class);
		var expected = new PageImpl<>(List.of(item), pageable, 1);

		given(productRepository.getAdminSearchList(
			keyword, categoryId, activeOnly, ProductSort.from(sort), pageable
		)).willReturn(expected);

		// when
		var result = adminProductService.getAdminSearchList(keyword, categoryId, activeOnly, sort, pageable);

		// then
		assertThat(result).isSameAs(expected);
		then(productRepository).should()
			.getAdminSearchList(keyword, categoryId, activeOnly, ProductSort.from(sort), pageable);
	}

	@Test
	void 관리자_상품_상세_조회_실패__상품_없으면_예외() {
		// given
		// 상품 없음
		var productId = 1L;
		given(productRepository.existsById(productId)).willReturn(false);

		// when & then
		var ex = assertThrowsExactly(CustomException.class,
			() -> adminProductService.getAdminDetailById(productId));

		assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_PRODUCT);
		then(productRepository).should().existsById(productId);
		then(productRepository).should(never()).findAdminDetailById(anyLong());
	}

	@Test
	void 관리자_상품_상세_조회_성공() {
		// given
		// 상품 존재
		var productId = 1L;
		given(productRepository.existsById(productId)).willReturn(true);

		// 상품 상세 정보
		var category = new Category("상의", null);
		var projection = mock(AdminProductDetailQueryResponse.class);

		given(projection.id()).willReturn(productId);
		given(projection.name()).willReturn("반팔티");
		given(projection.price()).willReturn(15000L);
		given(projection.description()).willReturn("베이직 반팔티");
		given(projection.color()).willReturn("WHITE");
		given(projection.stock()).willReturn(10L);
		given(projection.status()).willReturn(ProductStatus.ACTIVATED);
		given(projection.discountValue()).willReturn(1000L);
		given(projection.discountType()).willReturn(DiscountType.FIXED);
		given(projection.discountedPrice()).willReturn(14000L);
		given(projection.category()).willReturn(category);
		given(productRepository.findAdminDetailById(productId)).willReturn(projection);

		// 상품 카테고리
		var categories = List.of(mock(CategoryDetailResponse.class));
		given(categoryService.getCategoryHierarchy(category)).willReturn(categories);

		// when
		var result = adminProductService.getAdminDetailById(productId);

		// then
		assertThat(result.id()).isEqualTo(productId);
		assertThat(result.name()).isEqualTo("반팔티");
		assertThat(result.price()).isEqualTo(15000L);
		assertThat(result.description()).isEqualTo("베이직 반팔티");
		assertThat(result.color()).isEqualTo("WHITE");
		assertThat(result.stock()).isEqualTo(10L);
		assertThat(result.status()).isEqualTo(ProductStatus.ACTIVATED);
		assertThat(result.discountValue()).isEqualTo(1000L);
		assertThat(result.discountType()).isEqualTo(DiscountType.FIXED);
		assertThat(result.discountedPrice()).isEqualTo(14000L);
		assertThat(result.categories()).isEqualTo(categories);

		then(productRepository).should().existsById(productId);
		then(productRepository).should().findAdminDetailById(productId);
		then(categoryService).should().getCategoryHierarchy(category);
	}

	@Test
	void 관리자_상품_정보_수정_성공() {
		// given
		var productId = 1L;
		var categoryId = 2L;

		var name = "수정된 상품명";
		var price = 20000L;
		var description = "수정된 설명";
		var color = "BLACK";
		var stock = 50L;
		var status = "IN_ACTIVATED";

		// 상품 및 카테고리 조회
		var product = mock(Product.class);
		var category = mock(Category.class);

		given(productRepository.findByIdOrThrow(productId)).willReturn(product);
		given(categoryRepository.findByIdOrThrow(categoryId, ErrorCode.NOT_FOUND_CATEGORY))
			.willReturn(category);

		given(product.getId()).willReturn(productId);
		given(product.getName()).willReturn(name);
		given(product.getPrice()).willReturn(price);
		given(product.getDescription()).willReturn(description);
		given(product.getStock()).willReturn(stock);

		// when
		var result = adminProductService.updateDetail(
			productId, name, price, description, color, stock, status, categoryId
		);

		// then
		assertThat(result.id()).isEqualTo(productId);
		assertThat(result.name()).isEqualTo(name);
		assertThat(result.price()).isEqualTo(price);
		assertThat(result.description()).isEqualTo(description);
		assertThat(result.stock()).isEqualTo(stock);

		then(productRepository).should().findByIdOrThrow(productId);
		then(categoryRepository).should().findByIdOrThrow(categoryId, ErrorCode.NOT_FOUND_CATEGORY);
		then(product).should().update(
			name, price, description, color, stock, ProductStatus.from(status), category
		);
	}

	@Test
	void 관리자_상품_상태_활성화_성공() {
		// given
		var productId = 1L;
		var product = mock(Product.class);

		given(productRepository.findByIdOrThrow(productId)).willReturn(product);
		given(product.getId()).willReturn(productId);
		given(product.getStatus()).willReturn(ProductStatus.ACTIVATED);

		// when
		var result = adminProductService.updateActivated(productId);

		// then
		assertThat(result.productId()).isEqualTo(productId);
		assertThat(result.status()).isEqualTo(ProductStatus.ACTIVATED.name());

		then(productRepository).should().findByIdOrThrow(productId);
		then(product).should().activate();
	}

	@Test
	void 관리자_상품_상태_비활성화_성공() {
		// given
		var productId = 1L;
		var product = mock(Product.class);

		given(productRepository.findByIdOrThrow(productId)).willReturn(product);
		given(product.getId()).willReturn(productId);
		given(product.getStatus()).willReturn(ProductStatus.IN_ACTIVATED);

		// when
		var result = adminProductService.updateInActivated(productId);

		// then
		assertThat(result.productId()).isEqualTo(productId);
		assertThat(result.status()).isEqualTo(ProductStatus.IN_ACTIVATED.name());

		then(productRepository).should().findByIdOrThrow(productId);
		then(product).should().inActivate();
	}

	@Test
	void 관리자_상품_상태_품절_토글_성공() {
		// given
		var productId = 1L;
		var product = mock(Product.class);

		given(productRepository.findByIdOrThrow(productId)).willReturn(product);
		given(product.getId()).willReturn(productId);
		given(product.getStatus()).willReturn(ProductStatus.SOLD_OUT);

		// when
		var result = adminProductService.updateSoldOutToggle(productId);

		// then
		assertThat(result.productId()).isEqualTo(productId);
		assertThat(result.status()).isEqualTo(ProductStatus.SOLD_OUT.name());

		then(productRepository).should().findByIdOrThrow(productId);
		then(product).should().toggleSoldOut();
	}

	@Test
	void 관리자_상품_상태_다중_품절_실패__상품_일부_없음() {
		// given
		var ids = List.of(1L, 2L, 3L);

		// 조회 결과 개수 부족
		var product1 = mock(Product.class);
		var product2 = mock(Product.class);
		given(productRepository.findAllById(ids)).willReturn(List.of(product1, product2));

		// when & then
		var ex = assertThrowsExactly(CustomException.class,
			() -> adminProductService.updateSoldOutList(ids));

		assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND_PRODUCT);
		then(productRepository).should().findAllById(ids);
	}

	@Test
	void 관리자_상품_상태_다중_품절_성공() {
		// given
		var ids = List.of(1L, 2L);

		var p1 = mock(Product.class);
		var p2 = mock(Product.class);

		given(p1.getId()).willReturn(1L);
		given(p2.getId()).willReturn(2L);
		given(p1.getStatus()).willReturn(ProductStatus.SOLD_OUT);
		given(p2.getStatus()).willReturn(ProductStatus.SOLD_OUT);

		given(productRepository.findAllById(ids)).willReturn(List.of(p1, p2));

		// when
		var result = adminProductService.updateSoldOutList(ids);

		// then
		assertThat(result).hasSize(2);
		assertThat(result.get(0).productId()).isEqualTo(1L);
		assertThat(result.get(0).status()).isEqualTo(ProductStatus.SOLD_OUT.name());
		assertThat(result.get(1).productId()).isEqualTo(2L);
		assertThat(result.get(1).status()).isEqualTo(ProductStatus.SOLD_OUT.name());

		then(productRepository).should().findAllById(ids);
		then(p1).should().soldOut();
		then(p2).should().soldOut();
	}

	@Test
	void 관리자_상품_재고_목록_조회_성공() {
		// given
		var keyword = "반팔";
		var pageable = PageRequest.of(0, 10);

		// 재고 목록
		var item = mock(AdminProductStockResponse.class);
		var expected = new PageImpl<>(List.of(item), pageable, 1);

		given(productRepository.getStockList(keyword, pageable)).willReturn(expected);

		// when
		var result = adminProductService.getStockList(keyword, pageable);

		// then
		assertThat(result).isSameAs(expected);
		then(productRepository).should().getStockList(keyword, pageable);
	}

	@Test
	void 관리자_상품_재고_수정_성공() {
		// given
		var productId = 1L;
		var quantity = 20L;

		// 상품 조회
		var product = mock(Product.class);
		given(productRepository.findByIdOrThrow(productId)).willReturn(product);

		given(product.getId()).willReturn(productId);
		given(product.getName()).willReturn("반팔티");
		given(product.getPrice()).willReturn(15000L);
		given(product.getDescription()).willReturn("설명");
		given(product.getStock()).willReturn(quantity);

		// when
		var result = adminProductService.updateStock(productId, quantity);

		// then
		assertThat(result.id()).isEqualTo(productId);
		assertThat(result.name()).isEqualTo("반팔티");
		assertThat(result.price()).isEqualTo(15000L);
		assertThat(result.description()).isEqualTo("설명");
		assertThat(result.stock()).isEqualTo(quantity);

		then(productRepository).should().findByIdOrThrow(productId);
		then(product).should().updateStock(quantity);
	}

	@Test
	void 관리자_상품_삭제_성공() {
		// given
		var productId = 1L;

		// 상품 조회
		var product = mock(Product.class);
		given(productRepository.findByIdOrThrow(productId)).willReturn(product);
		given(product.getId()).willReturn(productId);
		given(product.getStatus()).willReturn(ProductStatus.DELETED);

		// when
		var result = adminProductService.deleteProduct(productId);

		// then
		assertThat(result.productId()).isEqualTo(productId);
		assertThat(result.status()).isEqualTo(ProductStatus.DELETED.name());

		then(productRepository).should().findByIdOrThrow(productId);
		then(product).should().delete();
	}
}
