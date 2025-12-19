package com.shop.repository.review.response;

import java.util.UUID;

import com.querydsl.core.annotations.QueryProjection;
import com.shop.domain.review.ReviewLikeType;

public record ReviewDetailQueryResponse(
	Long reviewId,
	String title,
	String content,
	Long orderProductId,
	UUID userUuid,
	Integer likeCount,
	Integer dislikeCount,
	ReviewLikeType reviewLikeType,
	AdminReviewQueryResponse adminReview
) {
	@QueryProjection
	public ReviewDetailQueryResponse {
	}
}

