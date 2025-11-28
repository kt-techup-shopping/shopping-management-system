package com.shop.service.product;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.shop.domain.category.model.Category;
import com.shop.domain.category.repository.CategoryRepository;
import com.shop.domain.product.model.Product;
import com.shop.domain.product.repository.ProductRepository;
import com.shop.domain.product.service.AdminProductService;

@SpringBootTest
class AdminProductServiceTest {

	@Autowired
	AdminProductService adminProductService;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@Test
	void 상품_동시_업데이트_락() throws InterruptedException {
		// given
		int repeatCount = 100;
		int threadCount = 30;

		var category = categoryRepository.save(
			new Category("테스트 카테고리", null)
		);

		var product = productRepository.save(
			// 초기 재고: 0
			new Product(
				"테스트 상품",
				100_000L,
				0L,
				"테스트 설명",
				"red",
				category
			)
		);

		var executorService = Executors.newFixedThreadPool(threadCount);
		var startLatch = new CountDownLatch(1);
		var doneLatch = new CountDownLatch(repeatCount);

		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failureCount = new AtomicInteger(0);

		for (int i = 0; i < repeatCount; i++) {
			int idx = i;
			executorService.submit(() -> {
				try {
					startLatch.await();

					long deltaStock = idx % 2 != 0 ? 2L : -1L;
					adminProductService.updateDetail(
						product.getId(),
						"테스트 상품",
						100_000L,
						"테스트 설명",
						"red",
						deltaStock,
						"ACTIVATED",
						category.getId()
					);
					successCount.incrementAndGet();
				} catch (Exception e) {
					failureCount.incrementAndGet();
				} finally {
					doneLatch.countDown();
				}
			});
		}

		startLatch.countDown();
		doneLatch.await(10, TimeUnit.SECONDS);
		executorService.shutdown();

		var updated = productRepository.findByIdOrThrow(product.getId());

		System.out.println("성공한 업데이트 수: " + successCount.get());
		System.out.println("실패한 업데이트 수: " + failureCount.get());
		System.out.println("최종 재고 수: " + updated.getStock());

		// 1~100
		// 홀수: 50개 → 50 * +2 = +100
		// 짝수: 50개 → 50 * -1 = -50
		// 총합 = +50 → 최종 재고 50이 나와야 함
		Assertions.assertThat(successCount.get()).isEqualTo(repeatCount);
		Assertions.assertThat(failureCount.get()).isEqualTo(0);
		Assertions.assertThat(updated.getStock()).isEqualTo(50L);
	}
}
