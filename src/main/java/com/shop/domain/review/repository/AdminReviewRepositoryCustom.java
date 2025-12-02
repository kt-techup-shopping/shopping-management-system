package com.shop.domain.review.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.shop.domain.review.response.AdminNoReviewQueryResponse;
import com.shop.domain.review.response.AdminReviewDetailQueryResponse;

public interface AdminReviewRepositoryCustom {
	List<AdminReviewDetailQueryResponse> findAllReviewsWithAdmin(Pageable pageable);
	List<AdminNoReviewQueryResponse> findReviewsWithoutAdmin(Pageable pageable);
	long countAllReviews();
	long countReviewsWithoutAdmin();
}
