package com.shop.domain.review.response;

import java.util.UUID;

public record AdminReviewCreateAndUpdateResponse (
	Long reviewId,
	String title,
	String content,
	Long productId,
	UUID userUuid,  // String -> UUID 로 변경
	Integer likeCount,  // int -> Integer
	Integer dislikeCount,
	String adminReviewTitle,
	String adminReviewContent
){

}
