package com.shop.review.response;

import java.util.UUID;

import com.shop.domain.review.ReviewLikeType;
import com.shop.repository.review.response.AdminReviewQueryResponse;

public record ReviewResponse(
	Long reviewId,
	String title,
	String content,
	Long orderProductId,
	UUID userUuid,  // String -> UUID 로 변경
	Integer likeCount,  // int -> Integer
	Integer dislikeCount,
	ReviewLikeType reviewLikeType,
	AdminReviewQueryResponse adminReview
) {

}
