package com.shop.domain.review.response;

import java.util.UUID;

public record AdminReviewDetailResponse(
	Long reviewId,
	String reviewTitle,
	String reviewContent,
	UUID userUuid,
	AdminReviewQueryResponse adminReview  // 1:1 매핑
) {}
