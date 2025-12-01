package com.shop.domain.review.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.shop.domain.review.request.AdminReviewCreateRequest;
import com.shop.domain.review.request.AdminReviewUpdateRequest;
import com.shop.domain.review.service.AdminReviewService;
import com.shop.global.common.ApiResult;
import com.shop.global.security.DefaultCurrentUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

	private final AdminReviewService adminReviewService;

	/**
	 * 관리자 리뷰 등록
	 * 하나의 리뷰당 하나만 작성 가능
	 */
	@PostMapping("/{reviewId}")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResult<Void> createAdminReview(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@RequestBody @Valid AdminReviewCreateRequest adminReviewCreateRequest,
		@PathVariable Long reviewId
	) {
		adminReviewService.createAdminReview(
			adminReviewCreateRequest,
			reviewId,
			defaultCurrentUser.getId()
		);
		return ApiResult.ok();
	}

	/**
	 * 관리자 리뷰 수정
	 */
	@PutMapping("/{reviewId}/update")
	public ApiResult<Void> updateAdminReview(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@RequestBody @Valid AdminReviewUpdateRequest adminReviewUpdateRequest,
		@PathVariable Long reviewId
	) {
		adminReviewService.updateAdminReview(
			adminReviewUpdateRequest,
			reviewId,
			defaultCurrentUser.getId()
		);
		return ApiResult.ok();
	}

	/**
	 * 관리자 리뷰 삭제
	 */
	@PutMapping("/{reviewId}/delete")
	public ApiResult<Void> deleteAdminReview(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@PathVariable Long reviewId
	) {
		adminReviewService.deleteAdminReview(
			reviewId,
			defaultCurrentUser.getId()
		);
		return ApiResult.ok();
	}

	/**
	 * 사용자 리뷰 삭제
	 */
	@PutMapping("/{reviewId}/force-delete")
	public ApiResult<Void> deleteReview(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@PathVariable Long reviewId
	) {
		adminReviewService.deleteReview(
			reviewId,
			defaultCurrentUser.getId()
		);
		return ApiResult.ok();
	}
}
