package com.shop.domain.product;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import com.shop.domain.category.model.Category;
import com.shop.domain.product.model.Product;
import com.shop.domain.product.model.ProductStatus;
import com.shop.global.common.CustomException;

public class ProductTest {

	private Category parentCategory;
	private Category childCategory;

	@BeforeEach
	void setUp() {
		parentCategory = new Category("상의", null);
		childCategory = new Category("티셔츠", parentCategory);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 상품_생성_실패__상품명_null_이거나_공백(String name) {
		assertThrowsExactly(CustomException.class,
			() -> new Product(name,
				10000L,
				10L,
				"설명",
				"RED",
				childCategory
			));
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 상품_생성_실패__설명_null_이거나_공백(String description) {
		assertThrowsExactly(CustomException.class,
			() -> new Product(
				"상품명",
				10000L,
				10L,
				description,
				"RED",
				childCategory
			));
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 상품_생성_실패__색상_null_이거나_공백(String color) {
		assertThrowsExactly(CustomException.class,
			() -> new Product(
				"상품명",
				10000L,
				10L,
				"설명",
				color,
				childCategory
			));
	}

	@Test
	void 상품_생성_실패__가격이_음수() {
		assertThrowsExactly(CustomException.class,
			() -> new Product(
				"상품명",
				-1L,
				10L,
				"설명",
				"RED",
				childCategory
			));
	}

	@Test
	void 재고_설정_성공() {
		var product = new Product(
			"반팔티",
			15000L,
			10L,
			"설명",
			"WHITE",
			childCategory
		);

		product.updateStock(20L);

		assertThat(product.getStock()).isEqualTo(20L);
	}

	@Test
	void 상품_생성_실패__재고가_음수() {
		assertThrowsExactly(CustomException.class,
			() -> new Product(
				"상품명",
				10000L,
				-1L,
				"설명",
				"RED",
				childCategory
			));
	}

	@Test
	void 상품_생성_성공() {
		var product = new Product(
			"반팔티",
			15000L,
			30L,
			"베이직 반팔티",
			"WHITE",
			childCategory
		);

		assertThat(product.getName()).isEqualTo("반팔티");
		assertThat(product.getPrice()).isEqualTo(15000L);
		assertThat(product.getStock()).isEqualTo(30L);
		assertThat(product.getDescription()).isEqualTo("베이직 반팔티");
		assertThat(product.getColor()).isEqualTo("WHITE");
		assertThat(product.getCategory()).isEqualTo(childCategory);
		assertThat(product.getCategory().getParent()).isEqualTo(parentCategory);
		assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVATED);
	}

	@Test
	void 상품_정보_수정_성공() {
		var product = new Product(
			"반팔티",
			15000L,
			30L,
			"편한 반팔티",
			"WHITE",
			childCategory
		);

		var newCategory = new Category("스웨터", parentCategory);

		product.update(
			"스웨터",
			20000L,
			"따뜻한 스웨터",
			"BLACK",
			50L,
			ProductStatus.IN_ACTIVATED,
			newCategory
		);

		assertThat(product.getName()).isEqualTo("스웨터");
		assertThat(product.getPrice()).isEqualTo(20000L);
		assertThat(product.getDescription()).isEqualTo("따뜻한 스웨터");
		assertThat(product.getColor()).isEqualTo("BLACK");
		assertThat(product.getStock()).isEqualTo(50L);
		assertThat(product.getStatus()).isEqualTo(ProductStatus.IN_ACTIVATED);
		assertThat(product.getCategory()).isEqualTo(newCategory);
	}

	@Test
	void 상품_정보_수정_성공__재고_수량_0이고_상태_SOLD_OUT() {
		var product = new Product(
			"반팔티",
			15000L,
			10L,
			"설명",
			"WHITE",
			childCategory
		);

		product.update(
			"반팔티",
			15000L,
			"설명",
			"WHITE",
			0L,
			ProductStatus.SOLD_OUT,
			childCategory
		);

		assertThat(product.getStock()).isEqualTo(0L);
		assertThat(product.getStatus()).isEqualTo(ProductStatus.SOLD_OUT);
		assertThat(product.isSoldOut()).isTrue();
	}

	@Test
	void 상품_정보_수정_실패__수량이_음수() {
		var product = new Product(
			"반팔티",
			15000L,
			30L,
			"설명",
			"BLACK",
			childCategory
		);

		assertThrowsExactly(CustomException.class,
			() -> product.update(
				"이름",
				10000L,
				"설명2",
				"WHITE",
				-1L,
				ProductStatus.ACTIVATED,
				childCategory)
		);
	}

	@Test
	void 상품_정보_수정_실패__재고_수량_0인데_상태가_SOLD_OUT_아님() {
		var product = new Product(
			"반팔티",
			15000L,
			10L,
			"설명",
			"WHITE",
			childCategory
		);

		assertThrowsExactly(CustomException.class,
			() -> product.update(
				"반팔티",
				15000L,
				"설명",
				"WHITE",
				0L,
				ProductStatus.ACTIVATED,
				childCategory)
		);
	}

	@Test
	void 재고_설정_실패__음수() {
		var product = new Product(
			"반팔티",
			15000L,
			10L,
			"설명",
			"WHITE",
			childCategory
		);

		assertThrowsExactly(CustomException.class,
			() -> product.updateStock(-1L)
		);
	}

	@Test
	void 상태값_변경_성공() {
		var product = new Product(
			"반팔티",
			15000L,
			10L,
			"설명",
			"WHITE",
			childCategory
		);

		product.inActivate();
		assertThat(product.getStatus()).isEqualTo(ProductStatus.IN_ACTIVATED);

		product.soldOut();
		assertThat(product.getStatus()).isEqualTo(ProductStatus.SOLD_OUT);

		product.activate();
		assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVATED);

		product.delete();
		assertThat(product.getStatus()).isEqualTo(ProductStatus.DELETED);
		assertThat(product.getIsDeleted()).isTrue();
	}

	@Test
	void 상태_품절_토글() {
		var product = new Product(
			"반팔티",
			15000L,
			10L,
			"설명",
			"WHITE",
			childCategory
		);

		product.toggleSoldOut();
		assertThat(product.getStatus()).isEqualTo(ProductStatus.SOLD_OUT);

		product.toggleSoldOut();
		assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVATED);
	}
}
