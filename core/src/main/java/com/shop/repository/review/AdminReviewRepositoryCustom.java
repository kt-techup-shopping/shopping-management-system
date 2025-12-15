package com.shop.repository.review;

import java.util.List;

import org.springframework.data.domain.Pageable;


public interface AdminReviewRepositoryCustom {
	List<AdminReviewDetailQueryResponse> findAllReviewsWithAdmin(Pageable pageable);
	List<AdminNoReviewQueryResponse> findReviewsWithoutAdmin(Pageable pageable);
	long countAllReviews();
	long countReviewsWithoutAdmin();
}
