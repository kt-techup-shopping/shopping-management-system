package com.shop.review.response;

import java.util.UUID;

import com.shop.repository.review.response.AdminReviewQueryResponse;

public record AdminReviewDetailResponse(
	Long reviewId,
	String reviewTitle,
	String reviewContent,
	UUID userUuid,
	AdminReviewQueryResponse adminReview  // 1:1 매핑
) {}
