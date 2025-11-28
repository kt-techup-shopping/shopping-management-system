package com.shop.domain.review.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.review.model.AdminReview;
import com.shop.domain.review.repository.AdminReviewRepository;
import com.shop.domain.review.repository.ReviewRepository;
import com.shop.domain.review.request.AdminReviewCreateRequest;
import com.shop.domain.review.request.AdminReviewUpdateRequest;
import com.shop.domain.user.repository.UserRepository;
import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;
import com.shop.global.common.Preconditions;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminReviewService {

	private final AdminReviewRepository adminReviewRepository;
	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;

	/**
	 * 어드민 리뷰를 작성하는 API
	 */
	@Transactional
	public void createAdminReview(AdminReviewCreateRequest adminReviewCreateRequest, Long reviewId, Long userId) {
		var review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

		// 삭제 여부 검사
		Preconditions.validate(!review.getIsDeleted(), ErrorCode.NOT_FOUND_REVIEW);

		// 하나의 리뷰에 대해 하나만 작성 가능
		Preconditions.validate(!adminReviewRepository.existsByReviewIdAndIsDeletedFalse(reviewId), ErrorCode.ALREADY_WRITE_ADMIN_REVIEW);

		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);
		var adminReview = new AdminReview(
			adminReviewCreateRequest.title(),
			adminReviewCreateRequest.content(),
			review, user);

		adminReviewRepository.save(adminReview);
	}

	/**
	 * 어드민 리뷰를 수정하는 API
	 */
	@Transactional
	public void updateAdminReview(AdminReviewUpdateRequest adminReviewUpdateRequest, Long reviewId, Long userId) {
		var review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

		// 삭제 여부 검사
		Preconditions.validate(!review.getIsDeleted(), ErrorCode.NOT_FOUND_REVIEW);

		var adminAdminReview = adminReviewRepository.findByReviewIdAndIsDeletedFalse(reviewId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ADMIN_REVIEW));


		userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		adminAdminReview.update(adminReviewUpdateRequest.title(), adminReviewUpdateRequest.content());
	}

	/**
	 * 어드민 유저의 리뷰를 삭제하는 API
	 */
	@Transactional
	public void deleteAdminReview(Long reviewId, Long userId){
		var adminAdminReview = adminReviewRepository.findByReviewIdAndIsDeletedFalse(reviewId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ADMIN_REVIEW));

		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		Preconditions.validate(user.equals(adminAdminReview.getUser()), ErrorCode.DOES_NOT_MATCH_USER_REVIEW);

		adminAdminReview.delete();
	}

	/**
	 * 사용자 리뷰를 삭제하는 API
	 */
	@Transactional
	public void deleteReview(Long reviewId, Long userId){
		var review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

		// 삭제 여부 검사
		Preconditions.validate(!review.getIsDeleted(), ErrorCode.NOT_FOUND_REVIEW);

		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		review.delete();
	}

}
