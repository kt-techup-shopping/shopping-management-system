package com.shop.review.response;

import java.time.LocalDateTime;

import com.shop.domain.review.Review;

public record ReviewCreateAndUpdateResponse (
	Long reviewId,
	String userUuid,
	String title,
	String content,
	Long productId,
	Integer likeCount,
	Integer disLikeCount,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
){
	public static ReviewCreateAndUpdateResponse from(Review review) {
		return new ReviewCreateAndUpdateResponse(
			review.getId(),
			review.getUser().getUuid().toString(),
			review.getTitle(),
			review.getContent(),
			review.getOrderProduct().getProduct().getId(),
			review.getLikeCount(),
			review.getDislikeCount(),
			review.getCreatedAt(),
			review.getUpdatedAt()
		);
	}
}
