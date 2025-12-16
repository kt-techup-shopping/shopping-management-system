package com.shop.repository.review;

import java.util.List;

import com.shop.repository.review.response.ReviewDetailQueryResponse;
import com.shop.repository.review.response.ReviewPageQueryResponse;

public interface ReviewRepositoryCustom {
	// 전체 리뷰 페이지 조회
	List<ReviewPageQueryResponse> findReviews(Long loginUserId, Long productId, int offset, int limit, String sort);

	// 특정 사용자 리뷰 페이지 조회
	List<ReviewPageQueryResponse> findReviewsByUser(Long targetUserId, Long loginUserId, int offset, int limit);

	// 전체 리뷰 수
	long countReviews();

	// 특정 사용자 리뷰 수
	long countReviewsByUser(Long userId);

	// 상품 별 리뷰 수
	long countReviewsByProduct(Long productId);

	// 리뷰 id로 조회
	ReviewDetailQueryResponse findReviewById(Long reviewId, Long loginUserId);
}

