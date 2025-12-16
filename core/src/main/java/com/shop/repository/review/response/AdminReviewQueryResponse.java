package com.shop.repository.review.response;

import com.querydsl.core.annotations.QueryProjection;

public record AdminReviewQueryResponse(
	String title,
	String content
) {
	@QueryProjection
	public AdminReviewQueryResponse {
	}
}
