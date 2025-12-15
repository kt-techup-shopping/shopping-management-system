package com.shop.repository.review;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.domain.review.Review;
import com.shop.domain.review.ReviewLike;
import com.shop.domain.user.User;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

	Optional<ReviewLike> findByReviewAndUserAndIsDeletedFalse(Review review, User user);
}
