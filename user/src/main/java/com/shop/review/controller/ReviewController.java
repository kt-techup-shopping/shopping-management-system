package com.shop.review.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shop.ApiResult;
import com.shop.ErrorCode;
import com.shop.Paging;
import com.shop.docs.ApiErrorCodeExample;
import com.shop.docs.ApiErrorCodeExamples;
import com.shop.review.request.ReviewCreateRequest;
import com.shop.review.request.ReviewLikeRequest;
import com.shop.review.request.ReviewUpdateRequest;
import com.shop.review.response.ReviewCreateAndUpdateResponse;
import com.shop.review.response.ReviewPageResponse;
import com.shop.review.response.ReviewResponse;
import com.shop.review.service.ReviewService;
import com.shop.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "리뷰", description = "상품 리뷰 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

	private final ReviewService reviewService;

	@Operation(summary = "리뷰 등록", description = "사용자가 구매한 상품에 대해 리뷰를 작성합니다. 하나의 상품당 하나의 리뷰만 작성 가능하며, 삭제 후 재작성 가능")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_PURCHASED_PRODUCT,
		ErrorCode.NOT_FOUND_USER,
		ErrorCode.ALREADY_WRITE_REVIEW
	})
	@PostMapping("/{orderProductId}")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResult<ReviewCreateAndUpdateResponse> createReview(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@RequestBody @Valid ReviewCreateRequest reviewCreateRequest,
		@PathVariable Long orderProductId
	) {
		return ApiResult.ok(reviewService.createReview(reviewCreateRequest, orderProductId, defaultCurrentUser.getId()));
	}

	@Operation(summary = "리뷰 삭제", description = "사용자가 작성한 리뷰를 삭제합니다.")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_FOUND_REVIEW,
		ErrorCode.NOT_FOUND_USER,
		ErrorCode.DOES_NOT_MATCH_USER_REVIEW,
	})
	@PutMapping("/{reviewId}/delete")
	public ApiResult<Void> deleteReview(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@PathVariable Long reviewId
	) {
		reviewService.deleteReview(reviewId, defaultCurrentUser.getId());
		return ApiResult.ok();
	}

	@Operation(summary = "리뷰 수정", description = "삭제되지 않은 리뷰만 수정 가능합니다.")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_FOUND_REVIEW,
		ErrorCode.NOT_FOUND_USER,
		ErrorCode.DOES_NOT_MATCH_USER_REVIEW,
	})
	@PutMapping("/{reviewId}/update")
	public ApiResult<ReviewCreateAndUpdateResponse> updateReview(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@RequestBody @Valid ReviewUpdateRequest reviewUpdateRequest,
		@PathVariable Long reviewId
	) {
		return ApiResult.ok(reviewService.updateReview(reviewUpdateRequest, reviewId, defaultCurrentUser.getId()));
	}

	@Operation(summary = "리뷰 좋아요/좋아요 취소", description = "단일 API로 좋아요 상태를 토글할 수 있습니다.")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_FOUND_REVIEW,
		ErrorCode.NOT_FOUND_USER,
	})
	@PutMapping("/{reviewId}/like")
	public ApiResult<ReviewCreateAndUpdateResponse> updateReviewLike(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@RequestBody @Valid ReviewLikeRequest reviewLikeRequest,
		@PathVariable Long reviewId
	) {
		return ApiResult.ok(reviewService.updateReviewLike(reviewLikeRequest, reviewId, defaultCurrentUser.getId()));
	}

	@Operation(summary = "상품 리뷰 조회", description = "특정 상품에 대한 리뷰 목록을 조회합니다. 정렬(좋아요순, 최신순, 오래된순)과 페이지네이션 지원")
	@SecurityRequirements(value = {})
	@GetMapping("")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Page<ReviewPageResponse>> getReviews(
		@RequestParam Long productId,
		@RequestParam(required = false, defaultValue = "oldest") String sort,
		@Parameter Paging paging,
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser
	) {
		Long userId = Optional.ofNullable(defaultCurrentUser)
			.map(DefaultCurrentUser::getId)
			.orElse(null);

		Page<ReviewPageResponse> reviewPage = reviewService.getReviewPage(userId, productId, sort, paging.toPageable());

		return ApiResult.ok(reviewPage);
	}

	@Operation(summary = "사용자 리뷰 조회", description = "특정 사용자가 작성한 리뷰 목록을 조회합니다. 페이지네이션 지원")
	@SecurityRequirements(value = {})
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_USER)
	@GetMapping("/user")
	public ApiResult<Page<ReviewPageResponse>> getUserReviews(
		@RequestParam String userUUID,
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@Parameter Paging paging
	) {
		Long userId = Optional.ofNullable(defaultCurrentUser)
			.map(DefaultCurrentUser::getId)
			.orElse(null);
		return ApiResult.ok(reviewService.getUserReviewsByUuid(userUUID, userId, paging.toPageable()));
	}

	@Operation(summary = "단일 리뷰 조회", description = "리뷰 ID를 통해 단일 리뷰를 조회합니다.")
	@SecurityRequirements(value = {})
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_REVIEW)
	@GetMapping("/single")
	public ApiResult<ReviewResponse> getReview(
		@RequestParam Long reviewId,
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser
	) {
		Long userId = Optional.ofNullable(defaultCurrentUser)
			.map(DefaultCurrentUser::getId)
			.orElse(null);
		return ApiResult.ok(reviewService.getReview(reviewId, userId));
	}

}

