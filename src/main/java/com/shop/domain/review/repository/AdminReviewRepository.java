package com.shop.domain.review.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.domain.review.model.AdminReview;

public interface AdminReviewRepository extends JpaRepository<AdminReview, Long>, AdminReviewRepositoryCustom{

	Optional<AdminReview> findByReviewIdAndIsDeletedFalse(Long reviewId);
	boolean existsByReviewIdAndIsDeletedFalse(Long reviewId);

}
