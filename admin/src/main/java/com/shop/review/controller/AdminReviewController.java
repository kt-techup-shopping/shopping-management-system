package com.shop.review.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shop.ApiResult;
import com.shop.ErrorCode;
import com.shop.Paging;
import com.shop.docs.ApiErrorCodeExamples;
import com.shop.review.request.AdminReviewCreateRequest;
import com.shop.review.request.AdminReviewUpdateRequest;
import com.shop.review.response.AdminNoReviewResponse;
import com.shop.review.response.AdminReviewCreateAndUpdateResponse;
import com.shop.review.response.AdminReviewDetailResponse;
import com.shop.review.service.AdminReviewService;
import com.shop.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "관리자 리뷰", description = "관리자가 리뷰를 관리하는 API")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/reviews")
public class AdminReviewController {

	private final AdminReviewService adminReviewService;

	@Operation(summary = "관리자 리뷰 등록", description = "하나의 리뷰에 대해 관리자 리뷰를 등록합니다.")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_FOUND_REVIEW,
		ErrorCode.ALREADY_WRITE_ADMIN_REVIEW,
		ErrorCode.NOT_FOUND_USER,
	})
	@PostMapping("/{reviewId}")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResult<AdminReviewCreateAndUpdateResponse> createAdminReview(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@RequestBody @Valid AdminReviewCreateRequest adminReviewCreateRequest,
		@PathVariable Long reviewId
	) {
		return ApiResult.ok(adminReviewService.createAdminReview(
			adminReviewCreateRequest,
			reviewId,
			defaultCurrentUser.getId()
		));
	}

	// 작성자 외 다른 어드민도 되게 하는게 맞는지 확인.(그렇다면 ID업데이트 해줘야하는지)
	@Operation(summary = "관리자 리뷰 수정", description = "등록된 관리자 리뷰를 수정합니다.")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_FOUND_REVIEW,
		ErrorCode.NOT_FOUND_ADMIN_REVIEW,
		ErrorCode.NOT_FOUND_USER,
		ErrorCode.DOES_NOT_MATCH_USER_REVIEW
	})
	@PutMapping("/{reviewId}/update")
	public ApiResult<AdminReviewCreateAndUpdateResponse> updateAdminReview(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@RequestBody @Valid AdminReviewUpdateRequest adminReviewUpdateRequest,
		@PathVariable Long reviewId
	) {
		return ApiResult.ok(adminReviewService.updateAdminReview(
			adminReviewUpdateRequest,
			reviewId,
			defaultCurrentUser.getId()
		));
	}

	// 삭제는 어떤 어드민이든 할 수 있지만, 수정은 본인만 가능
	@Operation(summary = "관리자 리뷰 삭제", description = "관리자 리뷰를 삭제합니다.")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_FOUND_REVIEW,
		ErrorCode.NOT_FOUND_ADMIN_REVIEW,
		ErrorCode.NOT_FOUND_USER,
	})
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

	@Operation(summary = "모든 리뷰 + 관리자 리뷰 조회", description = "모든 사용자 리뷰와 1:1로 매칭된 관리자 리뷰를 조회합니다. 페이징/정렬 가능")
	@GetMapping("/all")
	public ApiResult<Page<AdminReviewDetailResponse>> getAllReviewsWithAdmin(
		@Parameter Paging paging
	) {
		Page<AdminReviewDetailResponse> reviews = adminReviewService.getAllReviewsWithAdmin(paging.toPageable());
		return ApiResult.ok(reviews);
	}

	@Operation(summary = "관리자 리뷰 없는 리뷰 조회", description = "사용자 리뷰는 있지만 관리자 리뷰가 없는 리뷰만 조회합니다. 페이징/정렬 가능")
	@GetMapping("/no-admin")
	public ApiResult<Page<AdminNoReviewResponse>> getReviewsWithoutAdmin(
		@Parameter Paging paging
	) {
		Page<AdminNoReviewResponse> reviews = adminReviewService.getReviewsWithoutAdmin(paging.toPageable());
		return ApiResult.ok(reviews);
	}

	@Operation(summary = "사용자 리뷰 강제 삭제", description = "관리자가 사용자 리뷰를 강제로 삭제합니다.")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_FOUND_REVIEW,
		ErrorCode.NOT_FOUND_USER
	})
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

