package com.shop.domain.product.repository;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.domain.category.model.Category;
import com.shop.domain.category.repository.CategoryRepository;
import com.shop.domain.discount.model.DiscountType;
import com.shop.domain.discount.model.QDiscount;
import com.shop.domain.product.model.ProductStatus;
import com.shop.domain.product.model.QProduct;
import com.shop.domain.product.response.ProductDetailQueryResponse;
import com.shop.domain.product.response.ProductSearchResponse;
import com.shop.domain.product.response.QProductDetailQueryResponse;
import com.shop.domain.product.response.QProductSearchResponse;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

	private final CategoryRepository categoryRepository;
	private final JPAQueryFactory jpaQueryFactory;
	private final QProduct product = QProduct.product;
	private final QDiscount discount = QDiscount.discount;

	@Override
	public Page<ProductSearchResponse> search(String keyword, Long categoryId, Boolean activeOnly, String sort,
		PageRequest pageable) {
		var booleanBuilder = new BooleanBuilder();
		booleanBuilder.and(filterActive(activeOnly));
		booleanBuilder.and(containsProductName(keyword));
		booleanBuilder.and(categoryIn(categoryId));

		var content = jpaQueryFactory
			.select(new QProductSearchResponse(
				product.id,
				product.name,
				product.price,
				product.status,
				discount.value,
				discount.type,
				discountedPriceExpression()
			))
			.from(product)
			.leftJoin(discount)
			.on(discount.product.eq(product)
				.and(discount.id.eq(latestDiscountIdSubQuery()))
			)
			.where(booleanBuilder)
			.orderBy(resolveSort(sort))
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

	// 상품 상세 정보 조회
	@Override
	public ProductDetailQueryResponse findDetailById(Long id) {
		return jpaQueryFactory
			.select(new QProductDetailQueryResponse(
				product.id,
				product.name,
				product.price,
				product.description,
				product.color,
				product.status,
				product.category,
				discount.value,
				discount.type,
				discountedPriceExpression()
			))
			.from(product)
			// 최신 할인 정보 조회 위해 discount 테이블 left join
			.leftJoin(discount)
			.on(discount.product.eq(product)
				.and(discount.id.eq(latestDiscountIdSubQuery()))
			)
			.where(product.id.eq(id))
			.fetchOne();
	}

	// 상품명에 키워드가 포함되는지 확인
	private BooleanExpression containsProductName(String keyword) {
		return Strings.isNotBlank(keyword) ? product.name.containsIgnoreCase(keyword) : null;
	}

	// 판매중 필터링 여부
	private BooleanExpression filterActive(Boolean activeOnly) {
		return Boolean.TRUE.equals(activeOnly)
			? product.status.eq(ProductStatus.ACTIVATED) : null;
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

	// 정렬 기준
	private OrderSpecifier<?> resolveSort(String sort) {
		if (Strings.isBlank(sort)) {
			return product.id.desc();
		}

		return switch (sort) {
			case "priceAsc" -> product.price.asc();
			case "priceDesc" -> product.price.desc();
			case "latest" -> product.createdAt.desc();
			case "oldest" -> product.createdAt.asc();
			default -> product.id.desc();
		};
	}

	private SubQueryExpression<Long> latestDiscountIdSubQuery() {
		QDiscount sub = new QDiscount("subDiscount");
		return JPAExpressions
			.select(sub.id)
			.from(sub)
			.where(sub.product.eq(product))
			.orderBy(sub.createdAt.desc())
			.limit(1);
	}

	private NumberExpression<Long> discountedPriceExpression() {
		NumberExpression<Long> rate =
			product.price.subtract(product.price.multiply(discount.value).divide(100));

		NumberExpression<Long> amount =
			product.price.subtract(discount.value.longValue());

		return new CaseBuilder()
			.when(discount.type.eq(DiscountType.PERCENT)).then(rate)
			.when(discount.type.eq(DiscountType.FIXED)).then(amount)
			.otherwise(product.price);
	}

}
