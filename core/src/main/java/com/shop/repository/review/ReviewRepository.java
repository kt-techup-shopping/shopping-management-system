package com.shop.repository.review;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.domain.review.Review;

public interface ReviewRepository extends ReviewRepositoryCustom, JpaRepository<Review, Long> {

	Optional<Review> findById(Long id);

	Optional<Review> findByUserIdAndOrderProductIdAndIsDeletedFalse(Long userId, Long orderProductId);


	boolean existsByUserIdAndOrderProductIdAndIsDeletedFalse(Long userId, Long orderProductId);

	boolean existsByIdAndIsDeletedTrue(Long id);
}
