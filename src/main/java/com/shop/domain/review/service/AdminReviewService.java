package com.shop.domain.review.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.review.model.AdminReview;
import com.shop.domain.review.repository.AdminReviewRepository;
import com.shop.domain.review.repository.ReviewRepository;
import com.shop.domain.review.request.AdminReviewCreateRequest;
import com.shop.domain.review.request.AdminReviewUpdateRequest;
import com.shop.domain.review.response.AdminNoReviewQueryResponse;
import com.shop.domain.review.response.AdminReviewDetailQueryResponse;
import com.shop.domain.review.response.AdminReviewDetailResponse;
import com.shop.domain.review.response.AdminNoReviewResponse;
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

	@Transactional
	public Page<AdminReviewDetailResponse> getAllReviewsWithAdmin(PageRequest pageable) {
		// Repository에서 QueryResponse를 받아옴
		List<AdminReviewDetailQueryResponse> queryList = adminReviewRepository.findAllReviewsWithAdmin(pageable);

		// Service Response로 변환
		List<AdminReviewDetailResponse> responseList = queryList.stream()
			.map(q -> new AdminReviewDetailResponse(
				q.reviewId(),
				q.reviewTitle(),
				q.reviewContent(),
				q.userUuid(),
				q.adminReview() // AdminReviewQuery 그대로 사용
			))
			.toList();

		long total = adminReviewRepository.countAllReviews();
		return new PageImpl<>(responseList, pageable, total);
	}

	@Transactional
	public Page<AdminNoReviewResponse> getReviewsWithoutAdmin(PageRequest pageable) {
		List<AdminNoReviewQueryResponse> queryList = adminReviewRepository.findReviewsWithoutAdmin(pageable);

		List<AdminNoReviewResponse> responseList = queryList.stream()
			.map(q -> new AdminNoReviewResponse(
				q.reviewId(),
				q.reviewTitle(),
				q.reviewContent(),
				q.userUuid()
			))
			.toList();

		long total = adminReviewRepository.countReviewsWithoutAdmin();
		return new PageImpl<>(responseList, pageable, total);
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
