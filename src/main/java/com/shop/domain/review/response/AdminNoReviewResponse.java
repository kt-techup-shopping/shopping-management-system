package com.shop.domain.review.response;

import java.util.UUID;

public record AdminNoReviewResponse(
	Long reviewId,
	String reviewTitle,
	String reviewContent,
	UUID userUuid
) {
}
