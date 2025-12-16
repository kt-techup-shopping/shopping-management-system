package com.shop.repository.review.response;

import java.util.UUID;

import com.querydsl.core.annotations.QueryProjection;

public record AdminNoReviewQueryResponse (
	Long reviewId,
	String reviewTitle,
	String reviewContent,
	UUID userUuid
){
	@QueryProjection
	public AdminNoReviewQueryResponse{}
}
