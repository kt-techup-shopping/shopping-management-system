package com.shop.order.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.CustomException;
import com.shop.ErrorCode;
import com.shop.Lock;
import com.shop.Preconditions;
import com.shop.domain.order.Order;
import com.shop.domain.order.Receiver;
import com.shop.domain.orderproduct.OrderProduct;
import com.shop.order.request.OrderCreateRequest;
import com.shop.order.request.OrderDeleteRequest;
import com.shop.order.request.OrderUpdateRequest;
import com.shop.order.response.OrderCreateResponse;
import com.shop.order.response.OrderDetailResponse;
import com.shop.order.response.OrderDetailUserResponse;
import com.shop.repository.order.OrderRepository;
import com.shop.repository.order.response.OrderDetailQueryResponse;
import com.shop.repository.orderproduct.OrderProductRepository;
import com.shop.repository.product.ProductRepository;
import com.shop.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;
	private final OrderProductRepository orderProductRepository;

	@Lock(key = Lock.Key.STOCK, index = 1, isList = true)
	public OrderCreateResponse createOrder(
		Long userId,
		List<Long> productIds,
		OrderCreateRequest orderCreateRequest
	) {
		var products = productRepository.findAllByIdOrThrow(productIds);

		// 각 상품이 충분한 재고를 제공할 수 있는지 검증
		products.forEach(product ->
			Preconditions.validate(product.canProvide(orderCreateRequest.productQuantity().get(product.getId())),
				ErrorCode.NOT_ENOUGH_STOCK)
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
				var orderProduct = orderProductRepository.save(
					new OrderProduct(order, product, orderCreateRequest.productQuantity().get(product.getId())));

				// 재고 감소
				product.decreaseStock(orderCreateRequest.productQuantity().get(product.getId()));

				// 연관관계 편의 메서드 호출
				product.mapToOrderProduct(orderProduct);
				order.mapToOrderProduct(orderProduct);

				return orderProduct;
			})
			.toList();

		return OrderCreateResponse.from(order);
	}

	/**
	 * 내 모든 주문 내역을 가져오는 API
	 */
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

	/**
	 * 주문을 수정하는 API
	 */
	@Lock(key = Lock.Key.STOCK, index = 1, isList = true)
	public void updateOrder(
		Long userId,
		List<Long> productIds,
		OrderUpdateRequest orderUpdateRequest
	) {
		var products = productRepository.findAllByIdOrThrow(productIds);
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		var order = orderRepository.findByIdAndIsDeletedFalse(orderUpdateRequest.orderId())
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ORDER));

		// 본인 주문 상품에 대해서만 수정 가능
		Preconditions.validate(order.getUser().equals(user), ErrorCode.NOT_PURCHASED_PRODUCT);

		// Receiver 정보 업데이트
		order.getReceiver().update(
			orderUpdateRequest.receiverName(),
			orderUpdateRequest.receiverAddress(),
			orderUpdateRequest.receiverMobile()
		);

		// 기존 OrderProduct 정리 (재고 복구 + 논리삭제)
		order.getOrderProducts().stream()
			.filter(op -> !op.getIsDeleted() && productIds.contains(op.getProduct().getId())) // 아직 삭제되지 않은 것만
			.forEach(op -> {
				var product = op.getProduct();

				// 1) 기존 수량만큼 재고 되돌리기
				product.increaseStock(op.getQuantity());

				// 2) 논리삭제
				op.cancel();
			});

		// 새 OrderProduct 생성 + 재고 차감
		var newOrderProducts = products.stream()
			.map(product -> {
				Long newQuantity = orderUpdateRequest.productQuantity().get(product.getId());
				Preconditions.validate(
					product.canProvide(newQuantity),
					ErrorCode.NOT_ENOUGH_STOCK
				);
				// 재고 차감
				product.decreaseStock(newQuantity);

				var orderProduct = new OrderProduct(order, product, newQuantity);
				orderProductRepository.save(orderProduct);

				// 양방향 연관관계 맵핑
				order.mapToOrderProduct(orderProduct);
				product.mapToOrderProduct(orderProduct);

				return orderProduct;
			}).toList();
	}

	/**
	 * 주문을 삭제하는 API
	 */
	@Lock(key = Lock.Key.STOCK, index = 1, isList = true)
	public void deleteOrder(Long userId, List<Long> productIds, OrderDeleteRequest orderDeleteRequest) {

		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		var order = orderRepository.findByIdAndIsDeletedFalse(orderDeleteRequest.orderId())
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ORDER));

		// 본인 주문만 삭제 가능
		Preconditions.validate(order.getUser().equals(user), ErrorCode.NOT_PURCHASED_PRODUCT);

		// 1) OrderProduct 재고 복구 + 논리 삭제
		order.getOrderProducts().stream()
			.filter(op -> !op.getIsDeleted()) // 이미 삭제된 건 제외
			.forEach(op -> {
				var product = op.getProduct();

				// 재고 되돌리기
				product.increaseStock(op.getQuantity());

				// OrderProduct 논리삭제
				op.cancel();
			});

		// 2) Order 논리삭제
		order.cancel();
	}

	/**
	 * 내 주문 상세 내역을 가져오는 API
	 */
	public OrderDetailUserResponse getMyOrderDetail(Long userId, Long orderId) {
		// 유저 및 주문 유효성 검사
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);
		var order = orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER);
		Preconditions.validate(user.getId().equals(order.getUser().getId()), ErrorCode.INVALID_ORDER_OWNER);

		// 주문 상세 내역 쿼리 조회
		var queryResults = orderRepository.findOrderDetailByUserIdAndOrderId(user.getId(), order.getId());

		// 주문 공통 정보 추출
		var orderInfo = queryResults.stream()
			.findFirst()
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ORDER));

		// 주문 상품 목록 변환
		var products = queryResults.stream()
			.map(r -> new OrderDetailUserResponse.OrderProductInfo(
				r.productId(),
				r.productName(),
				r.productPrice(),
				r.quantity()
			)).toList();

		// 결제 정보 변환
		var paymentInfo = new OrderDetailUserResponse.PaymentInfo(
			orderInfo.paymentAmount(),
			orderInfo.deliveryFee(),
			orderInfo.paymentType()
		);

		// 최종 반환
		return new OrderDetailUserResponse(
			orderInfo.orderId(),
			orderInfo.receiverName(),
			orderInfo.receiverAddress(),
			orderInfo.receiverMobile(),
			orderInfo.orderStatus(),
			orderInfo.deliveredAt(),
			orderInfo.orderedAt(),
			products,
			paymentInfo
		);
	}

}
