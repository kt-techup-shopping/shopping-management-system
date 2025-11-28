package com.shop.domain.review.response;

import com.querydsl.core.annotations.QueryProjection;

public record AdminReviewQueryResponse(
	String title,
	String content
) {
	@QueryProjection
	public AdminReviewQueryResponse {
	}
}
