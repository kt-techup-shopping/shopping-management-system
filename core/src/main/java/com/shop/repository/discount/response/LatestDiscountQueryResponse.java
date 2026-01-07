package com.shop.repository.discount.response;

import com.querydsl.core.annotations.QueryProjection;
import com.shop.domain.discount.DiscountType;

public record LatestDiscountQueryResponse(
	Long productId,
	Long value,
	DiscountType type
) {
	@QueryProjection
	public LatestDiscountQueryResponse {
	}
}
