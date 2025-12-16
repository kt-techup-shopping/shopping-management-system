package com.shop.order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shop.ApiResult;
import com.shop.ErrorCode;
import com.shop.docs.ApiErrorCodeExample;
import com.shop.docs.ApiErrorCodeExamples;
import com.shop.order.request.OrderCreateRequest;
import com.shop.order.request.OrderDeleteRequest;
import com.shop.order.request.OrderUpdateRequest;
import com.shop.order.response.OrderDetailResponse;
import com.shop.order.response.OrderDetailUserResponse;
import com.shop.order.service.OrderService;
import com.shop.payment.request.PaymentCreateRequest;
import com.shop.payment.response.PaymentResponse;
import com.shop.payment.service.PaymentService;
import com.shop.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "주문", description = "주문 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
	private final OrderService orderService;
	private final PaymentService paymentService;

	//주문생성
	@Operation(summary = "주문 생성", description = "사용자가 상품을 선택하여 주문을 생성합니다.")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_ENOUGH_STOCK,
		ErrorCode.NOT_FOUND_USER,
	})
	@PostMapping
	public ApiResult<Void> createOrder(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@RequestBody @Valid OrderCreateRequest orderCreateRequest) {
		orderService.createOrder(
			defaultCurrentUser.getId(),
			// lock을 위해 리스트로
			orderCreateRequest.productQuantity().keySet().stream().toList(),
			orderCreateRequest
		);
		return ApiResult.ok();
	}

	@Operation(summary = "내 주문 조회", description = "사용자가 자신의 모든 주문 내역을 조회합니다.")
	@GetMapping
	public ApiResult<List<OrderDetailResponse>> getMyOrders(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser) {

		List<OrderDetailResponse> orders = orderService.getMyOrders(defaultCurrentUser.getId());
		return ApiResult.ok(orders);
	}

	@Operation(summary = "주문 수정", description = "이미 생성한 주문의 수령인 정보 및 상품 정보를 수정합니다.")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_FOUND_USER,
		ErrorCode.NOT_FOUND_ORDER,
		ErrorCode.NOT_PURCHASED_PRODUCT,
	})
	@PutMapping("/update")
	public ApiResult<Void> updateOrder(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@RequestBody @Valid OrderUpdateRequest orderUpdateRequest) {
		orderService.updateOrder(
			defaultCurrentUser.getId(),
			// lock을 위해 리스트로
			orderUpdateRequest.productQuantity().keySet().stream().toList(),
			orderUpdateRequest
		);
		return ApiResult.ok();
	}

	@Operation(summary = "주문 삭제", description = "이미 생성한 주문의 수령인 정보 및 상품 정보를 삭제합니다.")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_FOUND_USER,
		ErrorCode.NOT_FOUND_ORDER,
		ErrorCode.NOT_PURCHASED_PRODUCT,
	})
	@PutMapping("/delete")
	public ApiResult<Void> deleteOrder(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@RequestBody @Valid OrderDeleteRequest orderDeleteRequest
	) {
		orderService.deleteOrder(
			defaultCurrentUser.getId(),
			orderDeleteRequest.productIds(),
			orderDeleteRequest
		);
		return ApiResult.ok();
	}

	@Operation(summary = "내 주문 상세 조회", description = "사용자가 자신의 특정 주문 내역을 상세 조회합니다.")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_FOUND_USER,
		ErrorCode.NOT_FOUND_ORDER,
		ErrorCode.INVALID_ORDER_OWNER
	})
	@GetMapping("/{id}/detail")
	public ApiResult<OrderDetailUserResponse> getMyOrderDetail(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@PathVariable Long id
	) {
		return ApiResult.ok(orderService.getMyOrderDetail(defaultCurrentUser.getId(), id));
	}

	@Operation(summary = "결제 생성", description = "오더 ID를 통해 결제를 생성합니다.")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_FOUND_ORDER,
		ErrorCode.ALREADY_PAID_ORDER,
		ErrorCode.ALREADY_PENDING_ORDER,
	})
	@PostMapping("/{orderId}/payments")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResult<Void> createPayment(
		@PathVariable Long orderId,
		@RequestBody PaymentCreateRequest request
	) {
		paymentService.createPayment(orderId, request.type());
		return ApiResult.ok();
	}

	@Operation(summary = "결제 조회", description = "오더 ID를 통해 결제를 조회합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_ORDER)
	@GetMapping("/{orderId}/payments")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<List<PaymentResponse>> getPaymentInfo(@PathVariable Long orderId) {
		var payments = paymentService.getPayment(orderId);
		return ApiResult.ok(payments);
	}

}
