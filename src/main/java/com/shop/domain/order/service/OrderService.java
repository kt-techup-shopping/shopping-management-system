package com.shop.domain.order.service;

import java.util.List;
import java.util.stream.Collectors;

import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.order.model.Order;
import com.shop.domain.order.model.Receiver;
import com.shop.domain.order.request.OrderCreateRequest;
import com.shop.domain.order.response.OrderDetailQueryResponse;
import com.shop.domain.order.response.OrderDetailResponse;
import com.shop.global.common.ErrorCode;
import com.shop.global.common.Lock;
import com.shop.global.common.Preconditions;
import com.shop.domain.orderproduct.model.OrderProduct;
import com.shop.domain.order.repository.OrderRepository;
import com.shop.domain.orderproduct.repository.OrderProductRepository;
import com.shop.domain.product.repository.ProductRepository;
import com.shop.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
	private final RedisProperties redisProperties;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;
	private final OrderProductRepository orderProductRepository;
	private final RedissonClient redissonClient;

	// reference , primitive
	// 선택하는 기준 1번째 : null 가능?
	// Long -> null, long -> 0
	// Generic이냐 아니냐 -> Generic은 무조건 참조형
	//주문생성
	@Lock(key = Lock.Key.STOCK, index = 1, isList = true)
	public void createOrder(
		Long userId,
		List<Long> productIds,
		OrderCreateRequest orderCreateRequest
	) {
		var products = productRepository.findAllByIdOrThrow(productIds);

		// 각 상품이 충분한 재고를 제공할 수 있는지 검증
		products.forEach(product ->
			Preconditions.validate(product.canProvide(orderCreateRequest.productQuantity().get(product.getId())), ErrorCode.NOT_ENOUGH_STOCK)
		);


		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		var receiver = new Receiver(
			orderCreateRequest.receiverName(),
			orderCreateRequest.receiverAddress(),
			orderCreateRequest.receiverMobile()
		);

		var order = orderRepository.save(Order.create(receiver, user));

		var orderProducts = products.stream()
			.map(product -> {
				var orderProduct = orderProductRepository.save(new OrderProduct(order, product, orderCreateRequest.productQuantity().get(product.getId())));

				// 재고 감소
				product.decreaseStock(orderCreateRequest.productQuantity().get(product.getId()));

				// 연관관계 편의 메서드 호출
				product.mapToOrderProduct(orderProduct);
				order.mapToOrderProduct(orderProduct);

				return orderProduct;
			})
			.toList();
	}

	public List<OrderDetailResponse> getMyOrders(Long userId) {
		var queryResults = orderRepository.findOrderDetailByUserId(userId);

		// 주문별로 그룹핑
		var grouped = queryResults.stream()
			.collect(Collectors.groupingBy(OrderDetailQueryResponse::orderId));

		return grouped.entrySet().stream()
			.map(entry -> {
				var first = entry.getValue().get(0);
				var products = entry.getValue().stream()
					.map(qr -> new OrderDetailResponse.OrderProductInfo(
						qr.productName(),
						qr.productPrice(),
						qr.quantity()
					))
					.toList();

				return new OrderDetailResponse(
					first.orderId(),
					first.receiverName(),
					first.receiverAddress(),
					first.receiverMobile(),
					first.orderStatus(),
					first.deliveredAt(),
					products
				);
			})
			.toList();
	}

}
