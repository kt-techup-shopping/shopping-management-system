package com.shop.domain.review.controller;

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

import com.shop.domain.review.request.ReviewCreateRequest;
import com.shop.domain.review.request.ReviewLikeRequest;
import com.shop.domain.review.request.ReviewUpdateRequest;
import com.shop.domain.review.response.ReviewPageResponse;
import com.shop.domain.review.response.ReviewResponse;
import com.shop.domain.review.service.ReviewService;
import com.shop.domain.user.model.User;
import com.shop.global.common.ApiResult;
import com.shop.global.common.Paging;
import com.shop.global.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	/**
	 * 사용자가 리뷰를 등록하는 API
	 * 하나의 상품 구매 내역에 대해 하나의 리뷰만 작성 가능
	 * 삭제 후 재작성 가능
	 */
	@PostMapping("/{productId}")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResult<Void> createReview(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@RequestBody @Valid ReviewCreateRequest reviewCreateRequest,
		@PathVariable Long productId
	){
		reviewService.createReview(reviewCreateRequest, productId, defaultCurrentUser.getId());
		return ApiResult.ok();
	}

	/**
	 * 사용자가 리뷰를 삭제하는 API
	 */
	@PutMapping("/{reviewId}/delete")
	public ApiResult<Void> deleteReview(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@PathVariable Long reviewId
	){
		reviewService.deleteReview(reviewId, defaultCurrentUser.getId());
		return ApiResult.ok();
	}

	/**
	 * 사용자가 리뷰를 수정하는 API
	 * 리뷰가 삭제되지 않은 경우에만 가능
	 */
	@PutMapping("/{reviewId}/update")
	public ApiResult<Void> updateReview(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@RequestBody @Valid ReviewUpdateRequest reviewUpdateRequest,
		@PathVariable Long reviewId
	){
		reviewService.updateReview(reviewUpdateRequest, reviewId, defaultCurrentUser.getId());
		return ApiResult.ok();
	}

	/**
	 * 사용자가 좋아요를 설정하는 API
	 * 단일 API를 통한 모든 상태전환 가능
	 */
	@PutMapping("/{reviewId}/like")
	public ApiResult<Void> updateReviewLike(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@RequestBody @Valid ReviewLikeRequest reviewLikeRequest,
		@PathVariable Long reviewId
	){
		reviewService.updateReviewLike(reviewLikeRequest, reviewId, defaultCurrentUser.getId());
		return ApiResult.ok();
	}

	/**
	 * 특정 상품에 대한 리뷰를 조회하는 API
	 * 좋아요순, 최신순, 오래된순(기본값) 에 대한 정렬 가능
	 * 페이지네이션 적용
	 */
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

	/**
	 * 특정 사용자 리뷰 조회하는 API
	 * 페이지네이션 적용
	 */
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

	/**
	 * 단일 리뷰 조회하는 API
	 */
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
