package com.shop.service.order;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.shop.domain.order.request.OrderCreateRequest;
import com.shop.domain.order.service.OrderService;
import com.shop.domain.product.model.Product;
import com.shop.domain.user.model.Gender;
import com.shop.domain.user.model.Role;
import com.shop.domain.user.model.Status;
import com.shop.domain.user.model.User;
import com.shop.domain.order.repository.OrderRepository;
import com.shop.domain.orderproduct.repository.OrderProductRepository;
import com.shop.domain.product.repository.ProductRepository;
import com.shop.domain.user.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceTest {

	@Autowired
	private OrderService orderService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderProductRepository orderProductRepository;

	@BeforeEach
	void setUp() {
		orderProductRepository.deleteAll();
		orderRepository.deleteAll();
		productRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	void 주문_생성() {
		// given
		var user = userRepository.save(
			new User(
				"testuser",
				UUID.randomUUID(),
				"password",
				"Test User",
				"email",
				"010-0000-0000",
				Gender.MALE,
				LocalDate.now(),
				Role.USER,
				Status.ACTIVE
			)
		);

		var product = productRepository.save(
			new Product(
				"테스트 상품명",
				100_000L,
				10L
			)
		);

		// OrderCreateRequest 생성
		var orderRequest = new OrderCreateRequest(
			Map.of(product.getId(), 2L), // Map으로 수량 지정
			"수신자 이름",
			"수신자 주소",
			"010-1111-2222"
		);

		// when
		orderService.createOrder(user.getId(), new ArrayList<>(orderRequest.productQuantity().keySet()), orderRequest);

		// then
		var foundedProduct = productRepository.findByIdOrThrow(product.getId());
		var foundedOrder = orderRepository.findAll().stream().findFirst();

		assertThat(foundedProduct.getStock()).isEqualTo(8L);
		assertThat(foundedOrder).isPresent();
	}

	@Test
	void 동시에_100명_주문() throws Exception {
		var repeatCount = 500;
		var userList = new ArrayList<User>();

		for (int i = 0; i < repeatCount; i++) {
			userList.add(new User(
				"testuser-" + i,
				UUID.randomUUID(),
				"password",
				"Test User-" + i,
				"email-" + i,
				"010-0000-000" + i,
				Gender.MALE,
				LocalDate.now(),
				Role.USER,
				Status.ACTIVE
			));
		}

		var users = userRepository.saveAll(userList);

		var product = productRepository.save(
			new Product(
				"테스트 상품명",
				100_000L,
				10L
			)
		);

		productRepository.flush();

		var executorService = Executors.newFixedThreadPool(100);
		var countDownLatch = new CountDownLatch(repeatCount);
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failureCount = new AtomicInteger(0);

		for (int i = 0; i < repeatCount; i++) {
			int finalI = i;

			executorService.submit(() -> {
				try {
					var targetUser = users.get(finalI);

					// 요청 DTO 생성
					var orderRequest = new OrderCreateRequest(
						Map.of(product.getId(), 1L), // Map으로 수량 지정
						"수신자 이름",
						"수신자 주소",
						"010-1111-2222"
					);

					orderService.createOrder(targetUser.getId(), new ArrayList<>(orderRequest.productQuantity().keySet()), orderRequest);

					successCount.incrementAndGet();

				} catch (RuntimeException e) {
					failureCount.incrementAndGet();
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();
		executorService.shutdown();

		var foundedProduct = productRepository.findByIdOrThrow(product.getId());

		assertThat(successCount.get()).isEqualTo(10);
		assertThat(failureCount.get()).isEqualTo(490);
		assertThat(foundedProduct.getStock()).isEqualTo(0);
	}

	@Test
	void 동시에_여러상품_주문() throws Exception {
		// given
		var repeatCount = 5; // 동시에 5명만 테스트
		var userList = new ArrayList<User>();
		for (int i = 0; i < repeatCount; i++) {
			userList.add(new User(
				"multiuser-" + i,
				UUID.randomUUID(),
				"password",
				"Multi User-" + i,
				"email-" + i,
				"010-0000-000" + i,
				Gender.MALE,
				LocalDate.now(),
				Role.USER,
				Status.ACTIVE
			));
		}
		var users = userRepository.saveAll(userList);

		// 상품 여러 개 생성
		var product1 = productRepository.save(new Product("상품1", 50_000L, 5L));
		var product2 = productRepository.save(new Product("상품2", 30_000L, 3L));

		productRepository.flush();

		var executorService = Executors.newFixedThreadPool(repeatCount);
		var countDownLatch = new CountDownLatch(repeatCount);
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failureCount = new AtomicInteger(0);

		for (int i = 0; i < repeatCount; i++) {
			int finalI = i;
			executorService.submit(() -> {
				try {
					var targetUser = users.get(finalI);

					// 여러 상품 주문 DTO 생성
					var orderRequest = new OrderCreateRequest(
						Map.of(
							product1.getId(), 1L,
							product2.getId(), 1L
						),
						targetUser.getName(),
						"수신자 주소-" + finalI,
						"010-1111-22" + finalI
					);

					orderService.createOrder(targetUser.getId(), new ArrayList<>(orderRequest.productQuantity().keySet()), orderRequest);

					successCount.incrementAndGet();
				} catch (RuntimeException e) {
					failureCount.incrementAndGet();
				} finally {
					countDownLatch.countDown();
				}
			});
		}

		countDownLatch.await();
		executorService.shutdown();

		var foundedProduct1 = productRepository.findByIdOrThrow(product1.getId());
		var foundedProduct2 = productRepository.findByIdOrThrow(product2.getId());

		// product1 재고는 5개, 동시에 5명 주문 -> 모두 성공
		assertThat(successCount.get()).isEqualTo(3); // product2 재고 3개 제한으로 실패 발생
		assertThat(failureCount.get()).isEqualTo(2);
		assertThat(foundedProduct1.getStock()).isEqualTo(2L); // 5 - 3
		assertThat(foundedProduct2.getStock()).isEqualTo(0L); // 3 - 3
	}

}
