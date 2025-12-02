package com.shop.domain.order.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import com.shop.domain.order.model.OrderStatus;
import com.shop.domain.order.repository.OrderRepository;
import com.shop.domain.order.request.AdminOrderStatusChangeRequest;
import com.shop.domain.order.response.AdminOrderDetailQueryResponse;
import com.shop.domain.order.response.AdminOrderDetailResponse;

import com.shop.domain.order.response.AdminOrderDetailUserResponse;
import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;
import com.shop.global.common.Paging;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminOrderService {

	private final OrderRepository orderRepository;

	public Page<AdminOrderDetailResponse> getOrders(Long orderId, Long userId, String status, Paging paging) {
		var queryResults = orderRepository.findAdminOrderDetail(
			orderId,
			userId,
			OrderStatus.from(status),
			paging.toPageable()
		);

		var grouped = queryResults.getContent().stream()
			.collect(Collectors.groupingBy(AdminOrderDetailQueryResponse::orderId));

		List<AdminOrderDetailResponse> content = grouped.entrySet().stream()
			.sorted((e1, e2) -> e2.getKey().compareTo(e1.getKey()))
			.map(entry -> {
				var first = entry.getValue().getFirst();
				var products = entry.getValue().stream()
					.map(qr -> new AdminOrderDetailResponse.OrderProductInfo(
						qr.productName(),
						qr.productPrice(),
						qr.quantity()
					))
					.toList();

				return new AdminOrderDetailResponse(
					first.orderId(),
					first.userId(),
					first.receiverName(),
					first.receiverAddress(),
					first.receiverMobile(),
					first.orderStatus(),
					first.deliveredAt(),
					products
				);
			})
			.toList();

		return new PageImpl<>(
			content,
			queryResults.getPageable(),
			queryResults.getTotalElements()
		);
	}

	/**
	 * 관리자 주문 상세 정보 조회 API
	 */
	public AdminOrderDetailUserResponse getAdminOrderDetailById(Long orderId) {
		// 주문 유효성 검사
		var order = orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER);

		// 관리자 주문 상세 내역 쿼리 조회
		var queryResults = orderRepository.findAdminOrderDetailUserById(order.getId());

		// 주문 공통 정보 추출
		var orderInfo = queryResults.stream()
			.findFirst()
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ORDER));

		// 주문 상품 목록 변환
		var products = queryResults.stream()
			.map(r -> new AdminOrderDetailUserResponse.OrderProductInfo(
				r.productId(),
				r.productName(),
				r.productPrice(),
				r.quantity()
			))
			.toList();

		// 결제 정보 변환
		var paymentInfo = new AdminOrderDetailUserResponse.PaymentInfo(
			orderInfo.paymentAmount(),
			orderInfo.deliveryFee(),
			orderInfo.paymentType()
		);

		// 최종 반환
		return new AdminOrderDetailUserResponse(
			orderInfo.orderId(),
			orderInfo.userId(),
			orderInfo.userName(),
			orderInfo.userEmail(),
			orderInfo.userMobile(),
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

	public void updateOrderStatus(AdminOrderStatusChangeRequest adminOrderStatusChangeRequest, Long orderId){
		var order = orderRepository.findByIdAndIsDeletedFalse(orderId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ORDER));

		order.updateStatus(adminOrderStatusChangeRequest.orderStatus());
	}

	public void deleteOrder(Long orderId){
		var order = orderRepository.findByIdAndIsDeletedFalse(orderId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ORDER));

		order.updateStatus(OrderStatus.CANCELLED);
	}

}
