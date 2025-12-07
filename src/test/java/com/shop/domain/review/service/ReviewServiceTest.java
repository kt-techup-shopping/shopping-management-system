package com.shop.domain.review.service;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.shop.domain.category.model.Category;
import com.shop.domain.order.model.Order;
import com.shop.domain.order.model.Receiver;
import com.shop.domain.order.repository.OrderRepository;
import com.shop.domain.orderproduct.model.OrderProduct;
import com.shop.domain.orderproduct.repository.OrderProductRepository;
import com.shop.domain.product.model.Product;
import com.shop.domain.review.repository.ReviewLikeRepository;
import com.shop.domain.review.repository.ReviewRepository;

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

	}
}