package com.shop.repository.product;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.domain.category.Category;
import com.shop.domain.discount.DiscountType;
import com.shop.domain.discount.QDiscount;
import com.shop.domain.order.OrderStatus;
import com.shop.domain.order.QOrder;
import com.shop.domain.orderproduct.QOrderProduct;
import com.shop.domain.product.ProductSort;
import com.shop.domain.product.ProductStatus;
import com.shop.domain.product.QProduct;
import com.shop.repository.category.CategoryRepository;
import com.shop.repository.product.response.AdminProductDetailQueryResponse;
import com.shop.repository.product.response.AdminProductSearchQueryResponse;
import com.shop.repository.product.response.AdminProductStockQueryResponse;
import com.shop.repository.product.response.ProductDetailQueryResponse;
import com.shop.repository.product.response.ProductSearchQueryResponse;
import com.shop.repository.product.response.QAdminProductDetailQueryResponse;
import com.shop.repository.product.response.QAdminProductSearchQueryResponse;
import com.shop.repository.product.response.QAdminProductStockQueryResponse;
import com.shop.repository.product.response.QProductDetailQueryResponse;
import com.shop.repository.product.response.QProductSearchQueryResponse;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

	private final CategoryRepository categoryRepository;
	private final JPAQueryFactory jpaQueryFactory;
	private final QProduct product = QProduct.product;
	private final QDiscount discount = QDiscount.discount;
	private final QOrderProduct orderProduct = QOrderProduct.orderProduct;
	private final QOrder order = QOrder.order;

	// 사용자 상품 목록 조회 (검색/카테고리/판매중/정렬 조건 적용)
	@Override
	public Page<ProductSearchQueryResponse> getSearchList(
		String keyword,
		Long categoryId,
		Boolean activeOnly,
		ProductSort sort,
		PageRequest pageable
	) {
		return searchProducts(
			keyword,
			categoryId,
			activeOnly,
			sort,
			pageable,
			new QProductSearchQueryResponse(
				product.id,
				product.name,
				product.price,
				product.status,
				discount.value,
				discount.type,
				discountedPriceExpression()
			),
			true
		);
	}

	// 사용자 상품 상세 조회
	@Override
	public ProductDetailQueryResponse findDetailById(Long id) {
		return findProductDetail(
			id, new QProductDetailQueryResponse(
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
			),
			true
		);
	}

	// 관리자 상품 목록 조회 (검색/카테고리/판매중/정렬 조건 적용, 재고 포함)
	@Override
	public Page<AdminProductSearchQueryResponse> getAdminSearchList(
		String keyword,
		Long categoryId,
		Boolean activeOnly,
		ProductSort sort,
		PageRequest pageable
	) {
		return searchProducts(
			keyword,
			categoryId,
			activeOnly,
			sort,
			pageable,
			new QAdminProductSearchQueryResponse(
				product.id,
				product.name,
				product.price,
				product.stock,
				product.status,
				discount.value,
				discount.type,
				discountedPriceExpression()
			),
			false
		);
	}

	// 관리자 상품 상세 조회
	@Override
	public AdminProductDetailQueryResponse findAdminDetailById(Long id) {
		return findProductDetail(
			id, new QAdminProductDetailQueryResponse(
				product.id,
				product.name,
				product.price,
				product.description,
				product.color,
				product.stock,
				product.status,
				product.category,
				discount.value,
				discount.type,
				discountedPriceExpression()
			),
			false
		);
	}

	// 관리자 상품 재고 목록 조회 (검색 조건 적용)
	@Override
	public Page<AdminProductStockQueryResponse> getStockList(String keyword, PageRequest pageable) {
		var reservedStock = reservedStockExpression();
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(stockKeywordFilter(keyword)); 					// 숫자면 상품 ID, 아니면 상품명 검색
		builder.and(filterDeleted(false)); 							// 삭제된 상품 포함

		var content = jpaQueryFactory
			.select(new QAdminProductStockQueryResponse(
				product.id,
				product.name,
				product.stock.subtract(reservedStock), 				// 사용 가능한 재고 = 전체 재고 - 예약된 재고
				reservedStock, 										// 예약된 재고 (주문 상태 PENDING 또는 COMPLETED)
				product.stock 										// 전체 재고 = 사용 가능한 재고 + 예약된 재고
			))
			.from(product)
			.where(builder)
			.orderBy(product.id.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = jpaQueryFactory
			.select(product.id.count())
			.from(product)
			.where(builder)
			.fetchOne();

		return new PageImpl<>(content, pageable, total != null ? total : 0L);
	}

	// 공통 상품 검색 쿼리
	private <T> Page<T> searchProducts(
		String keyword,
		Long categoryId,
		Boolean activeOnly,
		ProductSort sort,
		PageRequest pageable,
		Expression<T> projection,
		boolean filterDeleted
	) {
		// 검색 조건 빌더
		var builder = new BooleanBuilder();
		builder.and(filterActive(activeOnly));
		builder.and(containsProductName(keyword));
		builder.and(categoryIn(categoryId));
		builder.and(filterDeleted(filterDeleted));

		var content = jpaQueryFactory
			.select(projection)
			.from(product)
			// 최신 할인 정보 조회 위해 discount 테이블 left join
			.leftJoin(discount)
			.on(discount.product.eq(product)
				.and(discount.id.eq(latestDiscountIdSubQuery()))
			)
			.where(builder)
			.orderBy(resolveSort(sort))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		var total = jpaQueryFactory
			.select(product.id.count())
			.from(product)
			.where(builder)
			.fetchOne();

		return new PageImpl<>(content, pageable, total != null ? total : 0L);
	}

	// 공통 상품 상세 조회 쿼리
	private <T> T findProductDetail(
		Long productId,
		Expression<T> projection,
		boolean filterDeleted
	) {
		var builder = new BooleanBuilder();
		builder.and(product.id.eq(productId));
		builder.and(filterDeleted(filterDeleted));

		return jpaQueryFactory
			.select(projection)
			.from(product)
			// 최신 할인 정보 조회 위해 discount 테이블 left join
			.leftJoin(discount)
			.on(discount.product.eq(product)
				.and(discount.id.eq(latestDiscountIdSubQuery()))
			)
			.where(builder)
			.fetchOne();
	}

	// 삭제된 상품 필터링 조건 생성
	private BooleanExpression filterDeleted(boolean filterDeleted) {
		return filterDeleted
			? product.isDeleted.isFalse()  // 삭제 제외
			: null;                        // 삭제 포함
	}

	// 상품명에 키워드가 포함되는지 검색 조건 생성
	private BooleanExpression containsProductName(String keyword) {
		return Strings.isNotBlank(keyword) ? product.name.containsIgnoreCase(keyword) : null;
	}

	// 판매중 상품만 필터링하는 조건 생성
	private BooleanExpression filterActive(Boolean activeOnly) {
		return Boolean.TRUE.equals(activeOnly)
			? product.status.eq(ProductStatus.ACTIVATED) : null;
	}

	// 카테고리 및 하위 카테고리 포함 필터
	private BooleanExpression categoryIn(Long categoryId) {
		// 카테고리 미지정 시 전체
		if (categoryId == null) {
			return null;
		}

		// 자식 카테고리 조회
		List<Category> children = categoryRepository.findByParentId(categoryId);

		// 상위 + 자식 카테고리 전체
		if (!children.isEmpty()) {
			List<Long> ids = new ArrayList<>();
			ids.add(categoryId);
			ids.addAll(children.stream().map(Category::getId).toList());
			return product.category.id.in(ids);
		}

		// 자식 없으면 해당 카테고리 매칭
		return product.category.id.eq(categoryId);
	}

	// 정렬 기준 -> OrderSpecifier 변환 (QueryDSL 정렬 메타데이터 객체)
	private OrderSpecifier<?> resolveSort(ProductSort sort) {
		return switch (sort) {
			case ProductSort.PRICE_ASC -> product.price.asc();
			case ProductSort.PRICE_DESC -> product.price.desc();
			case ProductSort.LATEST -> product.createdAt.desc();
			case ProductSort.DEFAULT -> product.id.asc();
		};
	}

	// 각 상품별 최신 할인 id를 조회하는 서브쿼리
	private SubQueryExpression<Long> latestDiscountIdSubQuery() {
		QDiscount sub = new QDiscount("subDiscount");
		return JPAExpressions
			.select(sub.id)
			.from(sub)
			.where(sub.product.eq(product))
			.orderBy(sub.createdAt.desc())
			.limit(1);
	}

	// 할인 타입에 따라 최종 할인 가격을 계산하는 표현식
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

	// 예약된 재고 수량을 계산하는 표현식 (주문 상태 PENDING 또는 COMPLETED 수량)
	private SubQueryExpression<Long> reservedStockExpression() {
		return JPAExpressions
			.select(orderProduct.quantity.sum().coalesce(0L))    // 없으면 0으로 처리
			.from(orderProduct)
			.join(orderProduct.order, order)
			.where(
				orderProduct.product.eq(product)
					.and(order.status.in(
						OrderStatus.PENDING,
						OrderStatus.COMPLETED
					))
			);
	}

	// Stock 키워드 필터링 (숫자면 상품 ID, 아니면 상품명)
	private BooleanExpression stockKeywordFilter(String keyword) {
		if (Strings.isBlank(keyword))
			return null;

		return keyword.chars().allMatch(Character::isDigit)
			? product.id.eq(Long.valueOf(keyword))
			: product.name.containsIgnoreCase(keyword);
	}

}
