package com.shop.domain.product.repository;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.domain.category.model.Category;
import com.shop.domain.category.repository.CategoryRepository;
import com.shop.domain.product.dto.response.ProductSearchResponse;
import com.shop.domain.product.dto.response.QProductSearchResponse;
import com.shop.domain.product.model.ProductStatus;
import com.shop.domain.product.model.QProduct;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

	private final CategoryRepository categoryRepository;
	private final JPAQueryFactory jpaQueryFactory;
	private final QProduct product = QProduct.product;

	@Override
	public Page<ProductSearchResponse> search(String keyword, Long categoryId, PageRequest pageable) {
		var booleanBuilder = new BooleanBuilder();
		booleanBuilder.and(isActive());
		booleanBuilder.and(containsProductName(keyword));
		booleanBuilder.and(categoryIn(categoryId));

		var content = jpaQueryFactory
			.select(new QProductSearchResponse(
				product.id,
				product.name,
				product.price,
				product.stock,
				product.status
			))
			.from(product)
			.where(booleanBuilder)
			.orderBy(product.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		var total = (long)jpaQueryFactory
			.select(product.id)
			.from(product)
			.where(booleanBuilder)
			.fetch().size();

		return new PageImpl<>(content, pageable, total);
	}

	// 상품명에 키워드가 포함되는지 확인
	private BooleanExpression containsProductName(String keyword) {
		return Strings.isNotBlank(keyword) ? product.name.containsIgnoreCase(keyword) : null;
	}

	// 판매중인 상품인지 확인
	private BooleanExpression isActive() {
		return product.status.eq(ProductStatus.ACTIVATED);
	}

	// 카테고리 필터링
	private BooleanExpression categoryIn(Long categoryId) {
		return categoryId != null ? categoryMatch(categoryId) : null;
	}

	// 카테고리 및 하위 카테고리 포함 여부 확인
	private BooleanExpression categoryMatch(Long categoryId) {
		List<Category> children = categoryRepository.findByParentId(categoryId);
		if (!children.isEmpty()) {
			List<Long> ids = new ArrayList<>();
			ids.add(categoryId);
			ids.addAll(children.stream().map(Category::getId).toList());
			return product.category.id.in(ids);
		}
		return product.category.id.eq(categoryId);
	}

}
