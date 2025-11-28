package com.shop.domain.review.response;

import com.querydsl.core.annotations.QueryProjection;
import java.util.UUID;
import com.shop.domain.review.model.ReviewLikeType;

public record ReviewPageResponse(
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
	public ReviewPageResponse { }
}
