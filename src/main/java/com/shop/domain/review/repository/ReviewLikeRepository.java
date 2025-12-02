package com.shop.domain.review.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.domain.review.model.Review;
import com.shop.domain.review.model.ReviewLike;
import com.shop.domain.user.model.User;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

	Optional<ReviewLike> findByReviewAndUserAndIsDeletedFalse(Review review, User user);
}
