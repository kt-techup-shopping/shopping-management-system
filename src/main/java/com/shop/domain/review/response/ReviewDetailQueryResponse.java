package com.shop.domain.review.response;

import java.util.UUID;

import com.querydsl.core.annotations.QueryProjection;
import com.shop.domain.review.model.ReviewLikeType;

public record ReviewDetailQueryResponse(
	Long reviewId,
	String title,
	String content,
	Long orderProductId,
	UUID userUuid,
	Integer likeCount,
	Integer dislikeCount,
	ReviewLikeType reviewLikeType
) {
	@QueryProjection
	public ReviewDetailQueryResponse {
	}
}

