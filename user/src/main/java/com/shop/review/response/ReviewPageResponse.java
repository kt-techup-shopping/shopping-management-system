package com.shop.review.response;

import java.util.UUID;

import com.querydsl.core.annotations.QueryProjection;
import com.shop.domain.review.ReviewLikeType;

public record ReviewPageResponse(
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
	public ReviewPageResponse { }
}
