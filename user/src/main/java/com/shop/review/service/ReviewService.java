package com.shop.review.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.CustomException;
import com.shop.ErrorCode;
import com.shop.Preconditions;
import com.shop.domain.review.Review;
import com.shop.domain.review.ReviewLike;
import com.shop.domain.review.ReviewLikeType;
import com.shop.domain.user.User;
import com.shop.repository.order.OrderRepository;
import com.shop.repository.orderproduct.OrderProductRepository;
import com.shop.repository.review.ReviewLikeRepository;
import com.shop.repository.review.ReviewRepository;
import com.shop.repository.review.response.ReviewDetailQueryResponse;
import com.shop.repository.review.response.ReviewPageQueryResponse;
import com.shop.repository.user.UserRepository;
import com.shop.review.request.ReviewCreateRequest;
import com.shop.review.request.ReviewLikeRequest;
import com.shop.review.request.ReviewUpdateRequest;
import com.shop.review.response.ReviewCreateAndUpdateResponse;
import com.shop.review.response.ReviewPageResponse;
import com.shop.review.response.ReviewResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final OrderProductRepository orderProductRepository;
	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final ReviewLikeRepository reviewLikeRepository;

	// TODO : Lock 필요성 고민

	/**
	 * 사용자가 리뷰를 등록하는 API
	 * 하나의 상품 구매 내역에 대해 하나의 리뷰만 작성 가능
	 * 삭제 후 재작성 가능
	 */
	@Transactional
	public ReviewCreateAndUpdateResponse createReview(ReviewCreateRequest reviewCreateRequest, Long orderProductId, Long userId) {
		var orderProduct = orderProductRepository
			.findById(orderProductId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_PURCHASED_PRODUCT));

		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		// 이미 리뷰를 작성했다면 에러처리
		Preconditions.validate(!reviewRepository.existsByUserIdAndOrderProductIdAndIsDeletedFalse(userId, orderProductId), ErrorCode.ALREADY_WRITE_REVIEW);

		// TODO : 구매한 사용자가 맞는지 확인

		var review = new Review(
			reviewCreateRequest.title(),
			reviewCreateRequest.content(),
			orderProduct,
			user
		);

		reviewRepository.save(review);

		return ReviewCreateAndUpdateResponse.from(review);
	}

	/**
	 * 사용자가 리뷰를 삭제하는 API
	 */
	@Transactional
	public void deleteReview(Long reviewId, Long userId) {
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
	public ReviewCreateAndUpdateResponse updateReview(ReviewUpdateRequest reviewUpdateRequest, Long reviewId, Long userId) {
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

		return ReviewCreateAndUpdateResponse.from(review);
	}

	/**
	 * 사용자의 리뷰에 대한 좋아요/싫어요를 처리하는 서비스
	 * 좋아요 등록 및 취소
	 * 싫여요 등록 및 취소
	 * 좋아요 <-> 싫어요 상태 전환
	 */
	@Transactional
	public ReviewCreateAndUpdateResponse updateReviewLike(ReviewLikeRequest reviewLikeRequest, Long reviewId, Long userId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REVIEW));

		Preconditions.validate(!review.getIsDeleted(), ErrorCode.NOT_FOUND_REVIEW);

		User user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		ReviewLikeType newType = reviewLikeRequest.reviewLikeType();

		ReviewLike existing = reviewLikeRepository.findByReviewAndUserAndIsDeletedFalse(review, user)
			.orElse(null);

		if (existing == null) {
			// 첫 클릭
			saveNewLike(review, user, newType);
			return ReviewCreateAndUpdateResponse.from(review);
		}

		ReviewLikeType currentType = existing.getReviewLikeType();

		if (currentType == newType) {
			// 같은 상태 클릭 → 취소
			cancelLike(existing, review, newType);
			return ReviewCreateAndUpdateResponse.from(review);
		}

		// 다른 상태 클릭 → 기존 취소 + 새 상태 적용
		cancelLike(existing, review, currentType);
		saveNewLike(review, user, newType);
		return ReviewCreateAndUpdateResponse.from(review);
	}

	/**
	 * 특정 상품에 대한 리뷰를 조회하는
	 * 좋아요순, 최신순, 오래된순(기본값) 에 대한 정렬 가능
	 * 페이지네이션 적용
	 */
	@Transactional
	public Page<ReviewPageResponse> getReviewPage(
		Long loginUserId,
		Long productId,
		String sort,
		PageRequest pageable
	) {
		List<ReviewPageQueryResponse> daoList = reviewRepository.findReviews(
			loginUserId,
			productId,
			pageable.getPageNumber(),
			pageable.getPageSize(),
			sort
		);

		List<ReviewPageResponse> dtoList = daoList.stream()
			.map(r -> new ReviewPageResponse(
				r.reviewId(),
				r.title(),
				r.content(),
				r.orderProductId(),
				r.userUuid(),
				r.likeCount(),
				r.dislikeCount(),
				r.reviewLikeType(),
				r.adminReview()
			))
			.toList();

		long totalCount = (productId == null)
			? reviewRepository.countReviews()
			: reviewRepository.countReviewsByProduct(productId);

		return new PageImpl<>(dtoList, pageable, totalCount);
	}

	/**
	 * 특정 사용자 리뷰 조회하는
	 * 페이지네이션 적용
	 */
	@Transactional
	public Page<ReviewPageResponse> getUserReviewsByUuid(
		String uuid, Long loginUserId, PageRequest pageable
	) {
		Long targetUserId = userRepository.findByUuid(UUID.fromString(uuid))
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER))
			.getId();

		List<ReviewPageQueryResponse> daoList = reviewRepository.findReviewsByUser(
			targetUserId,
			loginUserId,
			(int)pageable.getOffset(),
			pageable.getPageSize()
		);

		List<ReviewPageResponse> dtoList = daoList.stream()
			.map(r -> new ReviewPageResponse(
				r.reviewId(),
				r.title(),
				r.content(),
				r.orderProductId(),
				r.userUuid(),
				r.likeCount(),
				r.dislikeCount(),
				r.reviewLikeType(),
				r.adminReview()
			))
			.toList();

		long totalCount = reviewRepository.countReviewsByUser(targetUserId);

		return new PageImpl<>(dtoList, pageable, totalCount);
	}

	/**
	 * 단일 리뷰 조회하는 API
	 */
	@Transactional
	public ReviewResponse getReview(Long reviewId, Long loginUserId) {

		ReviewDetailQueryResponse dto = reviewRepository.findReviewById(reviewId, loginUserId);

		if (dto == null) {
			throw new CustomException(ErrorCode.NOT_FOUND_REVIEW);
		}

		return new ReviewResponse(
			dto.reviewId(),
			dto.title(),
			dto.content(),
			dto.orderProductId(),
			dto.userUuid(),
			dto.likeCount(),
			dto.dislikeCount(),
			dto.reviewLikeType(),
			dto.adminReview()
		);
	}

	// 새로운 like 정보를 저장
	private void saveNewLike(Review review, User user, ReviewLikeType reviewLikeType) {
		ReviewLike newLike = new ReviewLike(review, user, reviewLikeType);
		reviewLikeRepository.save(newLike);

		if (reviewLikeType == ReviewLikeType.LIKE)
			review.incrementLike();
		else if (reviewLikeType == ReviewLikeType.DISLIKE)
			review.incrementDislike();
	}

	// 좋아요 정보 취소
	private void cancelLike(ReviewLike like, Review review, ReviewLikeType reviewLikeType) {
		like.cancel();

		if (reviewLikeType == ReviewLikeType.LIKE)
			review.decrementLike();
		else if (reviewLikeType == ReviewLikeType.DISLIKE)
			review.decrementDislike();
	}

}
