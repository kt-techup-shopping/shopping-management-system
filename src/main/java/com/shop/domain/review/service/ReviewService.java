package com.shop.domain.review.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.orderproduct.model.OrderProduct;
import com.shop.domain.orderproduct.repository.OrderProductRepository;
import com.shop.domain.review.model.Review;
import com.shop.domain.review.repository.ReviewRepository;
import com.shop.domain.review.request.ReviewCreateRequest;
import com.shop.domain.review.request.ReviewUpdateRequest;
import com.shop.domain.user.repository.UserRepository;
import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;
import com.shop.global.common.Preconditions;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final OrderProductRepository orderProductRepository;
	private final UserRepository userRepository;

	// TODO : Lock 필요성 고민
	/**
	 * 사용자가 리뷰를 등록하는 API
	 * 하나의 상품 구매 내역에 대해 하나의 리뷰만 작성 가능
	 * 삭제 후 재작성 가능
	 */
	@Transactional
	public void createReview(ReviewCreateRequest reviewCreateRequest, Long orderProductId, Long userId){
		var orderProduct = orderProductRepository
			.findById(orderProductId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_PURCHASED_PRODUCT));

		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		// 이미 리뷰를 작성했다면 에러처리
		Preconditions.validate(!reviewRepository.existsByUserIdAndOrderProductIdAndIsDeletedFalse(userId, orderProductId), ErrorCode.ALREADY_WRITE_REVIEW);

		var review = new Review(
			reviewCreateRequest.title(),
			reviewCreateRequest.content(),
			orderProduct,
			user
		);

		reviewRepository.save(review);
	}

	/**
	 * 사용자가 리뷰를 삭제하는 API
	 */
	@Transactional
	public void deleteReview(Long reviewId, Long userId){
		var review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

		// 삭제 여부 검사
		Preconditions.validate(!review.getIsDeleted(), ErrorCode.NOT_FOUND_REVIEW);

		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		Preconditions.validate(user.equals(review.getUser()), ErrorCode.DOES_NOT_MATCH_USER_REVIEW);

		review.delete();
	}

	/**
	 * 사용자가 리뷰를 수정하는 API
	 * 리뷰가 삭제되지 않은 경우에만 가능
	 */
	@Transactional
	public void updateReview(ReviewUpdateRequest reviewUpdateRequest, Long reviewId, Long userId){
		var review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

		// 삭제 여부 검사
		Preconditions.validate(!review.getIsDeleted(), ErrorCode.NOT_FOUND_REVIEW);

		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		Preconditions.validate(user.equals(review.getUser()), ErrorCode.DOES_NOT_MATCH_USER_REVIEW);

		review.update(
			reviewUpdateRequest.title(),
			reviewUpdateRequest.content()
		);
	}
}
