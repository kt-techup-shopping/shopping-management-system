package com.shop.review.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.CustomException;
import com.shop.ErrorCode;
import com.shop.Preconditions;
import com.shop.domain.review.AdminReview;
import com.shop.repository.review.AdminReviewRepository;
import com.shop.repository.review.ReviewRepository;
import com.shop.repository.review.response.AdminNoReviewQueryResponse;
import com.shop.repository.review.response.AdminReviewDetailQueryResponse;
import com.shop.repository.user.UserRepository;
import com.shop.review.request.AdminReviewCreateRequest;
import com.shop.review.request.AdminReviewUpdateRequest;
import com.shop.review.response.AdminNoReviewResponse;
import com.shop.review.response.AdminReviewCreateAndUpdateResponse;
import com.shop.review.response.AdminReviewDetailResponse;

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
	public AdminReviewCreateAndUpdateResponse createAdminReview(AdminReviewCreateRequest adminReviewCreateRequest,
		Long reviewId, Long userId) {
		var review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

		// 삭제 여부 검사
		Preconditions.validate(!review.getIsDeleted(), ErrorCode.NOT_FOUND_REVIEW);

		// 하나의 리뷰에 대해 하나만 작성 가능
		Preconditions.validate(!adminReviewRepository.existsByReviewIdAndIsDeletedFalse(reviewId),
			ErrorCode.ALREADY_WRITE_ADMIN_REVIEW);

		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);
		var adminReview = new AdminReview(
			adminReviewCreateRequest.title(),
			adminReviewCreateRequest.content(),
			review, user);

		adminReviewRepository.save(adminReview);

		return new AdminReviewCreateAndUpdateResponse(
			review.getId(),
			review.getTitle(),
			review.getContent(),
			review.getOrderProduct().getProduct().getId(),
			review.getUser().getUuid(),
			review.getLikeCount(),
			review.getDislikeCount(),
			adminReview.getTitle(),
			adminReview.getContent()
		);
	}

	/**
	 * 어드민 리뷰를 수정하는 API
	 */
	@Transactional
	public AdminReviewCreateAndUpdateResponse updateAdminReview(AdminReviewUpdateRequest adminReviewUpdateRequest,
		Long reviewId, Long userId) {
		var review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

		// 삭제 여부 검사
		Preconditions.validate(!review.getIsDeleted(), ErrorCode.NOT_FOUND_REVIEW);

		var adminReview = adminReviewRepository.findByReviewIdAndIsDeletedFalse(reviewId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ADMIN_REVIEW));

		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		Preconditions.validate(user.equals(adminReview.getUser()), ErrorCode.DOES_NOT_MATCH_USER_REVIEW);

		adminReview.update(adminReviewUpdateRequest.title(), adminReviewUpdateRequest.content());

		return new AdminReviewCreateAndUpdateResponse(
			review.getId(),
			review.getTitle(),
			review.getContent(),
			review.getOrderProduct().getProduct().getId(),
			review.getUser().getUuid(),
			review.getLikeCount(),
			review.getDislikeCount(),
			adminReview.getTitle(),
			adminReview.getContent()
		);
	}

	/**
	 * 어드민 유저의 리뷰를 삭제하는 API
	 */
	@Transactional
	public void deleteAdminReview(Long reviewId, Long userId) {
		var review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

		// 삭제 여부 검사
		Preconditions.validate(!review.getIsDeleted(), ErrorCode.NOT_FOUND_REVIEW);

		var adminAdminReview = adminReviewRepository.findByReviewIdAndIsDeletedFalse(reviewId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ADMIN_REVIEW));

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
	}

	/**
	 * 사용자 리뷰를 삭제하는 API
	 */
	@Transactional
	public void deleteReview(Long reviewId, Long userId) {
		var review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

		// 삭제 여부 검사
		Preconditions.validate(!review.getIsDeleted(), ErrorCode.NOT_FOUND_REVIEW);

		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		review.delete();
	}

}
