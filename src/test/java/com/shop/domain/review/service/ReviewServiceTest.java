package com.shop.domain.review.service;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.shop.domain.category.model.Category;
import com.shop.domain.order.model.Order;
import com.shop.domain.order.model.Receiver;
import com.shop.domain.order.repository.OrderRepository;
import com.shop.domain.orderproduct.model.OrderProduct;
import com.shop.domain.orderproduct.repository.OrderProductRepository;
import com.shop.domain.product.model.Product;
import com.shop.domain.review.model.Review;
import com.shop.domain.review.model.ReviewLike;
import com.shop.domain.review.model.ReviewLikeType;
import com.shop.domain.review.repository.ReviewLikeRepository;
import com.shop.domain.review.repository.ReviewRepository;

import com.shop.domain.review.request.ReviewCreateRequest;
import com.shop.domain.review.request.ReviewLikeRequest;
import com.shop.domain.review.request.ReviewUpdateRequest;
import com.shop.domain.review.response.ReviewCreateAndUpdateResponse;
import com.shop.domain.review.response.ReviewDetailQueryResponse;
import com.shop.domain.review.response.ReviewPageQueryResponse;
import com.shop.domain.review.response.ReviewPageResponse;
import com.shop.domain.review.response.ReviewResponse;
import com.shop.domain.user.model.Gender;
import com.shop.domain.user.model.Role;
import com.shop.domain.user.model.Status;
import com.shop.domain.user.model.User;
import com.shop.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

	@InjectMocks
	private ReviewService reviewService;

	@Mock
	private ReviewRepository reviewRepository;
	@Mock
	private OrderProductRepository orderProductRepository;
	@Mock
	private OrderRepository orderRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private ReviewLikeRepository reviewLikeRepository;

	private User baseUser;
	private Category category;
	private Product product;
	private Order order;
	private OrderProduct orderProduct;

	@BeforeEach
	void setUp() {
		baseUser  = new User(
			"loginId", UUID.randomUUID(), "password",
			"name", "email", "010-1234-5678", Gender.MALE,
			LocalDate.of(2000, 1, 10), Role.USER, Status.ACTIVE);
		category = new Category("테스트 카테고리", null);
		product = new Product(
			"테스트 상품", 1000L, 100L, "테스트 상품 설명", "테스트 상품 색깔", category
		);
		order = Order.create(new Receiver("테스트 수령자", "테스트 주소", "010-8765-4321"), baseUser);
		orderProduct = new OrderProduct(order, product, 5L);

		// Entity의 ID 설정
		ReflectionTestUtils.setField(baseUser, "id", 1L);
		ReflectionTestUtils.setField(category, "id", 1L);
		ReflectionTestUtils.setField(product, "id", 1L);
		ReflectionTestUtils.setField(order, "id", 1L);
		ReflectionTestUtils.setField(orderProduct, "id", 1L);

	}@Test
	@DisplayName("성공 - 리뷰 생성")
	void createReview_Success() {
		// given
		ReviewCreateRequest request = new ReviewCreateRequest("제목", "내용");
		given(orderProductRepository.findById(orderProduct.getId())).willReturn(Optional.of(orderProduct));
		given(userRepository.findByIdOrThrow(eq(baseUser.getId()), any())).willReturn(baseUser);
		given(reviewRepository.existsByUserIdAndOrderProductIdAndIsDeletedFalse(baseUser.getId(), orderProduct.getId())).willReturn(false);

		// when
		ReviewCreateAndUpdateResponse response = reviewService.createReview(request, orderProduct.getId(),
			baseUser.getId());

		// then
		assertThat(response).isNotNull();
		assertThat(response.title()).isEqualTo(request.title());
		assertThat(response.content()).isEqualTo(request.content());
		assertThat(response.userUuid()).isEqualTo(baseUser.getUuid().toString());
		assertThat(response.productId()).isEqualTo(product.getId());
	}

	@Test
	@DisplayName("성공 - 리뷰 수정")
	void updateReview_Success(){
		// given
		ReviewUpdateRequest request = new ReviewUpdateRequest("수정된 제목", "수정된 내용");
		Review review = new Review("제목", "내용", orderProduct, baseUser);
		ReflectionTestUtils.setField(review, "id", 1L);

		given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));
		given(userRepository.findByIdOrThrow(eq(baseUser.getId()), any())).willReturn(baseUser);

		// when
		ReviewCreateAndUpdateResponse response = reviewService.updateReview(request, review.getId(), baseUser.getId());

		// then
		assertThat(response).isNotNull();
		assertThat(response.title()).isEqualTo(request.title());
		assertThat(response.content()).isEqualTo(request.content());
		assertThat(response.userUuid()).isEqualTo(baseUser.getUuid().toString());
		assertThat(response.productId()).isEqualTo(product.getId());
	}

	@Test
	@DisplayName("성공 - 리뷰 삭제")
	void deleteReview_Success(){
		// given
		Review review = new Review("제목", "내용", orderProduct, baseUser);
		ReflectionTestUtils.setField(review, "id", 1L);

		given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));
		given(userRepository.findByIdOrThrow(eq(baseUser.getId()), any())).willReturn(baseUser);

		// when
		reviewService.deleteReview(review.getId(), baseUser.getId());

		// then
		assertThat(review.getIsDeleted()).isTrue();
	}

	@Test
	@DisplayName("성공 - 리뷰 좋아요 : 무상태 -> 좋아요")
	void updateReviewLike_ToLikeFromNone_Success(){
		// given
		ReviewLikeRequest request = new ReviewLikeRequest(ReviewLikeType.LIKE);
		Review review = new Review("제목", "내용", orderProduct, baseUser);
		ReflectionTestUtils.setField(review, "id", 1L);

		given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));
		given(userRepository.findByIdOrThrow(eq(baseUser.getId()), any())).willReturn(baseUser);
		given(reviewLikeRepository.findByReviewAndUserAndIsDeletedFalse(review, baseUser)).willReturn(Optional.empty());

		// when
		ReviewCreateAndUpdateResponse response = reviewService.updateReviewLike(request, review.getId(),
			baseUser.getId());

		// then
		assertThat(response).isNotNull();
		assertThat(response.likeCount()).isEqualTo(review.getLikeCount());
		assertThat(response.disLikeCount()).isEqualTo(review.getDislikeCount());
		assertThat(review.getLikeCount()).isEqualTo(1); // 좋아요 수 증가
		assertThat(review.getDislikeCount()).isEqualTo(0); // 싫어요 수 유지
	}

	@Test
	@DisplayName("성공 - 리뷰 좋아요 : 무상태 -> 싫어요")
	void updateReviewLike_ToDislikeFromNone_Success(){
		// given
		ReviewLikeRequest request = new ReviewLikeRequest(ReviewLikeType.DISLIKE);
		Review review = new Review("제목", "내용", orderProduct, baseUser);
		ReflectionTestUtils.setField(review, "id", 1L);

		given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));
		given(userRepository.findByIdOrThrow(eq(baseUser.getId()), any())).willReturn(baseUser);
		given(reviewLikeRepository.findByReviewAndUserAndIsDeletedFalse(review, baseUser)).willReturn(Optional.empty());

		// when
		ReviewCreateAndUpdateResponse response = reviewService.updateReviewLike(request, review.getId(),
			baseUser.getId());

		// then
		assertThat(response).isNotNull();
		assertThat(response.likeCount()).isEqualTo(review.getLikeCount());
		assertThat(response.disLikeCount()).isEqualTo(review.getDislikeCount());
		assertThat(review.getLikeCount()).isEqualTo(0); // 좋아요 수 유지
		assertThat(review.getDislikeCount()).isEqualTo(1); // 싫어요 수 증가
	}

	@Test
	@DisplayName("성공 - 리뷰 좋아요 : 좋아요 -> 싫어요")
	void updateReviewLike_ToDislikeFromLike_Success(){
		// given
		ReviewLikeRequest request = new ReviewLikeRequest(ReviewLikeType.DISLIKE);
		Review review = new Review("제목", "내용", orderProduct, baseUser);
		ReflectionTestUtils.setField(review, "id", 1L);
		ReflectionTestUtils.setField(review, "likeCount", 1);
		ReviewLike reviewLike = new ReviewLike(review, baseUser, ReviewLikeType.LIKE);

		given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));
		given(userRepository.findByIdOrThrow(eq(baseUser.getId()), any())).willReturn(baseUser);
		given(reviewLikeRepository.findByReviewAndUserAndIsDeletedFalse(review, baseUser)).willReturn(Optional.of(reviewLike));

		// when
		ReviewCreateAndUpdateResponse response = reviewService.updateReviewLike(request, review.getId(),
			baseUser.getId());

		// then
		assertThat(response).isNotNull();
		assertThat(response.likeCount()).isEqualTo(review.getLikeCount());
		assertThat(response.disLikeCount()).isEqualTo(review.getDislikeCount());
		assertThat(review.getLikeCount()).isEqualTo(0); // 좋아요 수 감소
		assertThat(review.getDislikeCount()).isEqualTo(1); // 싫어요 수 증가
	}

	@Test
	@DisplayName("성공 - 리뷰 좋아요 : 싫어요 -> 좋아요")
	void updateReviewLike_ToLikeFromDislike_Success(){
		// given
		ReviewLikeRequest request = new ReviewLikeRequest(ReviewLikeType.LIKE);
		Review review = new Review("제목", "내용", orderProduct, baseUser);
		ReflectionTestUtils.setField(review, "id", 1L);
		ReflectionTestUtils.setField(review, "dislikeCount", 1);
		ReviewLike reviewLike = new ReviewLike(review, baseUser, ReviewLikeType.DISLIKE);

		given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));
		given(userRepository.findByIdOrThrow(eq(baseUser.getId()), any())).willReturn(baseUser);
		given(reviewLikeRepository.findByReviewAndUserAndIsDeletedFalse(review, baseUser)).willReturn(Optional.of(reviewLike));

		// when
		ReviewCreateAndUpdateResponse response = reviewService.updateReviewLike(request, review.getId(),
			baseUser.getId());

		// then
		assertThat(response).isNotNull();
		assertThat(response.likeCount()).isEqualTo(review.getLikeCount());
		assertThat(response.disLikeCount()).isEqualTo(review.getDislikeCount());
		assertThat(review.getLikeCount()).isEqualTo(1); // 좋아요 수 증가
		assertThat(review.getDislikeCount()).isEqualTo(0); // 싫어요 수 감소
	}

	@Test
	@DisplayName("성공 - 리뷰 좋아요 : 좋아요 -> 무상태")
	void updateReviewLike_ToNoneFromLike_Success(){
		// given
		ReviewLikeRequest request = new ReviewLikeRequest(ReviewLikeType.LIKE);
		Review review = new Review("제목", "내용", orderProduct, baseUser);
		ReflectionTestUtils.setField(review, "id", 1L);
		ReflectionTestUtils.setField(review, "likeCount", 1);
		ReviewLike reviewLike = new ReviewLike(review, baseUser, ReviewLikeType.LIKE);

		given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));
		given(userRepository.findByIdOrThrow(eq(baseUser.getId()), any())).willReturn(baseUser);
		given(reviewLikeRepository.findByReviewAndUserAndIsDeletedFalse(review, baseUser)).willReturn(Optional.of(reviewLike));

		// when
		ReviewCreateAndUpdateResponse response = reviewService.updateReviewLike(request, review.getId(),
			baseUser.getId());

		// then
		assertThat(response).isNotNull();
		assertThat(response.likeCount()).isEqualTo(review.getLikeCount());
		assertThat(response.disLikeCount()).isEqualTo(review.getDislikeCount());
		assertThat(review.getLikeCount()).isEqualTo(0); // 좋아요 수 감소
		assertThat(review.getDislikeCount()).isEqualTo(0); // 싫어요 수 유지
	}

	@Test
	@DisplayName("성공 - 리뷰 좋아요 : 싫어요 -> 무상태")
	void updateReviewLike_ToNoneFromDislike_Success(){
		// given
		ReviewLikeRequest request = new ReviewLikeRequest(ReviewLikeType.DISLIKE);
		Review review = new Review("제목", "내용", orderProduct, baseUser);
		ReflectionTestUtils.setField(review, "id", 1L);
		ReflectionTestUtils.setField(review, "dislikeCount", 1);
		ReviewLike reviewLike = new ReviewLike(review, baseUser, ReviewLikeType.DISLIKE);

		given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));
		given(userRepository.findByIdOrThrow(eq(baseUser.getId()), any())).willReturn(baseUser);
		given(reviewLikeRepository.findByReviewAndUserAndIsDeletedFalse(review, baseUser)).willReturn(Optional.of(reviewLike));

		// when
		ReviewCreateAndUpdateResponse response = reviewService.updateReviewLike(request, review.getId(),
			baseUser.getId());

		// then
		assertThat(response).isNotNull();
		assertThat(response.likeCount()).isEqualTo(review.getLikeCount());
		assertThat(response.disLikeCount()).isEqualTo(review.getDislikeCount());
		assertThat(review.getLikeCount()).isEqualTo(0); // 좋아요 수 유지
		assertThat(review.getDislikeCount()).isEqualTo(0); // 싫어요 수 감소
	}

	@Test
	@DisplayName("성공 - 단일 리뷰 조회")
	void getReview_Success() {
		// given
		Long reviewId = 1L;
		Long loginUserId = baseUser.getId();

		ReviewDetailQueryResponse dto = new ReviewDetailQueryResponse(
			reviewId,
			"제목",
			"내용",
			orderProduct.getId(),
			baseUser.getUuid(),
			5,    // likeCount
			2,    // dislikeCount
			ReviewLikeType.LIKE,  // 로그인 유저의 좋아요 상태
			null // admin 리뷰 여부
		);

		given(reviewRepository.findReviewById(reviewId, loginUserId))
			.willReturn(dto);

		// when
		ReviewResponse response = reviewService.getReview(reviewId, loginUserId);

		// then
		assertThat(response).isNotNull();
		assertThat(response.reviewId()).isEqualTo(dto.reviewId());
		assertThat(response.title()).isEqualTo(dto.title());
		assertThat(response.content()).isEqualTo(dto.content());
		assertThat(response.orderProductId()).isEqualTo(dto.orderProductId());
		assertThat(response.userUuid()).isEqualTo(dto.userUuid());
		assertThat(response.likeCount()).isEqualTo(dto.likeCount());
		assertThat(response.dislikeCount()).isEqualTo(dto.dislikeCount());
		assertThat(response.reviewLikeType()).isEqualTo(dto.reviewLikeType());
		assertThat(response.adminReview()).isEqualTo(dto.adminReview());
	}

	@Test
	@DisplayName("성공 - 특정 상품 리뷰 조회(정렬 + 페이지네이션)")
	void getReviewPage_WithProductId_Success() {
		// given
		Long loginUserId = baseUser.getId();
		Long productId = 10L;
		String sort = "latest"; // 최신순
		PageRequest pageable = PageRequest.of(0, 10);

		// Mock DAO 결과
		ReviewPageQueryResponse daoResponse = new ReviewPageQueryResponse(
			1L,
			"리뷰 제목",
			"리뷰 내용",
			orderProduct.getId(),
			baseUser.getUuid(),
			5,                      // likeCount
			1,                      // dislikeCount
			ReviewLikeType.LIKE,    // reviewLikeType
			null                   // adminReview
		);

		given(reviewRepository.findReviews(
			eq(loginUserId),
			eq(productId),
			eq(pageable.getPageNumber()),
			eq(pageable.getPageSize()),
			eq(sort)
		)).willReturn(List.of(daoResponse));

		given(reviewRepository.countReviewsByProduct(productId))
			.willReturn(1L);

		// when
		Page<ReviewPageResponse> result =
			reviewService.getReviewPage(loginUserId, productId, sort, pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().size()).isEqualTo(1);

		ReviewPageResponse response = result.getContent().get(0);

		assertThat(response.reviewId()).isEqualTo(daoResponse.reviewId());
		assertThat(response.title()).isEqualTo("리뷰 제목");
		assertThat(response.content()).isEqualTo("리뷰 내용");
		assertThat(response.orderProductId()).isEqualTo(orderProduct.getId());
		assertThat(response.userUuid()).isEqualTo(baseUser.getUuid());
		assertThat(response.likeCount()).isEqualTo(5);
		assertThat(response.dislikeCount()).isEqualTo(1);
		assertThat(response.reviewLikeType()).isEqualTo(ReviewLikeType.LIKE);
		assertThat(response.adminReview()).isNull();
	}
}