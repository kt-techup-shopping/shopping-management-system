package com.shop.repository.discount;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.domain.discount.QDiscount;
import com.shop.repository.discount.response.LatestDiscountQueryResponse;
import com.shop.repository.discount.response.QLatestDiscountQueryResponse;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DiscountRepositoryCustomImpl implements DiscountRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;
	private final QDiscount discount = QDiscount.discount;

	public List<LatestDiscountQueryResponse> fetchLatestDiscountsByProductIds(List<Long> productIds) {
		if (productIds.isEmpty()) return List.of();

		QDiscount d = QDiscount.discount;
		QDiscount sub = new QDiscount("sub");

		var maxCreatedAt = JPAExpressions
			.select(sub.createdAt.max())
			.from(sub)
			.where(sub.product.id.eq(d.product.id));

		return jpaQueryFactory
			.select(new QLatestDiscountQueryResponse(
				discount.product.id,
				discount.value,
				discount.type
			))
			.from(d)
			.where(d.product.id.in(productIds)
				.and(d.createdAt.eq(maxCreatedAt))
			)
			.fetch();
	}

}
