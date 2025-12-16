package com.shop.repository.review.response;

import java.util.UUID;

import com.querydsl.core.annotations.QueryProjection;

public record AdminReviewDetailQueryResponse (
	Long reviewId,
	String reviewTitle,
	String reviewContent,
	UUID userUuid,
	AdminReviewQueryResponse adminReview
){
	@QueryProjection
	public AdminReviewDetailQueryResponse{}
}
