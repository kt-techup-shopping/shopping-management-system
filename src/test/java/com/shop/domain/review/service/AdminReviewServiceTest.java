package com.shop.domain.review.service;

import static com.shop.domain.user.model.QUser.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

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
import com.shop.domain.orderproduct.model.OrderProduct;
import com.shop.domain.product.model.Product;
import com.shop.domain.review.model.AdminReview;
import com.shop.domain.review.model.Review;
import com.shop.domain.review.repository.AdminReviewRepository;
import com.shop.domain.review.repository.ReviewRepository;
import com.shop.domain.review.request.AdminReviewCreateRequest;
import com.shop.domain.review.request.AdminReviewUpdateRequest;
import com.shop.domain.review.response.AdminReviewCreateAndUpdateResponse;
import com.shop.domain.review.response.AdminReviewDetailQueryResponse;
import com.shop.domain.review.response.AdminReviewDetailResponse;
import com.shop.domain.review.response.AdminReviewQueryResponse;
import com.shop.domain.user.model.User;
import com.shop.domain.user.repository.UserRepository;
import com.shop.domain.user.model.Gender;
import com.shop.domain.user.model.Role;
import com.shop.domain.user.model.Status;
import com.shop.domain.user.model.User;
import com.shop.domain.user.repository.UserRepository;
import com.shop.global.common.ErrorCode;

@ExtendWith(MockitoExtension.class)
class AdminReviewServiceTest {

	@InjectMocks
	private AdminReviewService adminReviewService;

	@Mock
	private AdminReviewRepository adminReviewRepository;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private UserRepository userRepository;

	private User adminUser;
	private Category category;
	private Product product;
	private Order order;
	private OrderProduct orderProduct;
	private Review review;

	@BeforeEach
	void setUp() {
		adminUser  = new User(
			"loginId", UUID.randomUUID(), "password",
			"name", "email", "010-1234-5678", Gender.MALE,
			LocalDate.of(2000, 1, 10), Role.ADMIN, Status.ACTIVE);
		category = new Category("테스트 카테고리", null);
		product = new Product(
			"테스트 상품", 1000L, 100L, "테스트 상품 설명", "테스트 상품 색깔", category
		);
		order = Order.create(new Receiver("테스트 수령자", "테스트 주소", "010-8765-4321"), adminUser);
		orderProduct = new OrderProduct(order, product, 5L);
		review = new Review("제목", "내용", orderProduct, adminUser);

		// Entity의 ID 설정
		ReflectionTestUtils.setField(adminUser, "id", 1L);
		ReflectionTestUtils.setField(category, "id", 1L);
		ReflectionTestUtils.setField(product, "id", 1L);
		ReflectionTestUtils.setField(order, "id", 1L);
		ReflectionTestUtils.setField(orderProduct, "id", 1L);
		ReflectionTestUtils.setField(review, "id", 1L);
	}

	@Test
	@DisplayName("성공 - 어드민 리뷰 생성")
	void createAdminReview_success() {
		// given
		AdminReviewCreateRequest request = new AdminReviewCreateRequest("관리자 제목", "관리자 내용");

		when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));
		when(adminReviewRepository.existsByReviewIdAndIsDeletedFalse(review.getId())).thenReturn(false);
		when(userRepository.findByIdOrThrow(adminUser.getId(), ErrorCode.NOT_FOUND_USER)).thenReturn(adminUser);

		AdminReview adminReview = new AdminReview("관리자 제목", "관리자 내용", review, adminUser);
		when(adminReviewRepository.save(any(AdminReview.class))).thenReturn(adminReview);

		// when
		AdminReviewCreateAndUpdateResponse response =
			adminReviewService.createAdminReview(request, review.getId(), adminUser.getId());

		// then
		assertThat(response.reviewId()).isEqualTo(review.getId());
		assertThat(response.title()).isEqualTo(review.getTitle());
		assertThat(response.content()).isEqualTo(review.getContent());
		assertThat(response.productId()).isEqualTo(review.getOrderProduct().getProduct().getId());
		assertThat(response.userUuid()).isEqualTo(review.getUser().getUuid());
		assertThat(response.likeCount()).isEqualTo(review.getLikeCount());
		assertThat(response.dislikeCount()).isEqualTo(review.getDislikeCount());
		assertThat(response.adminReviewTitle()).isEqualTo(request.title());
		assertThat(response.adminReviewContent()).isEqualTo(request.content());
	}

	@Test
	@DisplayName("성공 - 어드민 리뷰 수정")
	void updateAdminReview_success() {
		// given
		AdminReviewUpdateRequest request = new AdminReviewUpdateRequest("수정된 관리자 제목", "수정된 관리자 내용");
		AdminReview adminReview = new AdminReview("관리자 제목", "관리자 내용", review, adminUser);

		when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));
		when(adminReviewRepository.findByReviewIdAndIsDeletedFalse(review.getId())).thenReturn(Optional.of(adminReview));
		when(userRepository.findByIdOrThrow(adminUser.getId(), ErrorCode.NOT_FOUND_USER)).thenReturn(adminUser);

		// when
		AdminReviewCreateAndUpdateResponse response =
			adminReviewService.updateAdminReview(request, review.getId(), adminUser.getId());

		// then
		assertThat(response.reviewId()).isEqualTo(review.getId());
		assertThat(response.title()).isEqualTo(review.getTitle());
		assertThat(response.content()).isEqualTo(review.getContent());
		assertThat(response.productId()).isEqualTo(review.getOrderProduct().getProduct().getId());
		assertThat(response.userUuid()).isEqualTo(review.getUser().getUuid());
		assertThat(response.likeCount()).isEqualTo(review.getLikeCount());
		assertThat(response.dislikeCount()).isEqualTo(review.getDislikeCount());
		assertThat(response.adminReviewTitle()).isEqualTo(request.title());
		assertThat(response.adminReviewContent()).isEqualTo(request.content());
	}

	@Test
	@DisplayName("성공 - 어드민 리뷰 삭제")
	void deleteAdminReview_success() {
		// given
		AdminReview adminReview = new AdminReview("관리자 제목", "관리자 내용", review, adminUser);
		ReflectionTestUtils.setField(adminReview, "id", 10L);

		when(reviewRepository.findById(review.getId()))
			.thenReturn(Optional.of(review));
		when(adminReviewRepository.findByReviewIdAndIsDeletedFalse(review.getId()))
			.thenReturn(Optional.of(adminReview));

		// when
		assertDoesNotThrow(() ->
			adminReviewService.deleteAdminReview(review.getId(), adminUser.getId()));

		// then
		assertTrue(adminReview.getIsDeleted());
	}

	@Test
	@DisplayName("성공 - 어드민 리뷰가 있는 리뷰 전체 조회")
	void getAllReviewsWithAdmin_success() {
		// given
		PageRequest pageable = PageRequest.of(0, 10);

		AdminReviewDetailQueryResponse queryResponse =
			new AdminReviewDetailQueryResponse(
				review.getId(),
				review.getTitle(),
				review.getContent(),
				review.getUser().getUuid(),
				new AdminReviewQueryResponse(
					"관리자 제목",
					"관리자 내용"
				)
			);

		when(adminReviewRepository.findAllReviewsWithAdmin(pageable))
			.thenReturn(List.of(queryResponse));
		when(adminReviewRepository.countAllReviews()).thenReturn(1L);

		// when
		Page<AdminReviewDetailResponse> result =
			adminReviewService.getAllReviewsWithAdmin(pageable);

		// then
		assertThat(result.getTotalElements()).isEqualTo(1);
		AdminReviewDetailResponse r = result.getContent().get(0);

		assertThat(r.reviewId()).isEqualTo(review.getId());
		assertThat(r.reviewTitle()).isEqualTo(review.getTitle());
		assertThat(r.reviewContent()).isEqualTo(review.getContent());
		assertThat(r.userUuid()).isEqualTo(review.getUser().getUuid());
		assertThat(r.adminReview().title()).isEqualTo("관리자 제목");
		assertThat(r.adminReview().content()).isEqualTo("관리자 내용");
	}

}