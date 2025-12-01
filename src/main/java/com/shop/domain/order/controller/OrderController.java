package com.shop.domain.order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.order.request.OrderCreateRequest;
import com.shop.domain.order.service.OrderService;
import com.shop.domain.payment.request.PaymentCreateRequest;
import com.shop.domain.payment.response.PaymentResponse;
import com.shop.domain.payment.service.PaymentService;
import com.shop.global.common.ApiResult;
import com.shop.domain.order.request.OrderRequest;
import com.shop.global.security.DefaultCurrentUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;
	private final PaymentService paymentService;

	//주문생성
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

	@PostMapping("/{orderId}/payments")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResult<Void> createPayment(
		@PathVariable Long orderId,
		@RequestBody PaymentCreateRequest request
	) {
		paymentService.createPayment(orderId, request.type());

		return ApiResult.ok();
	}

	@GetMapping("/{orderId}/payments")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<List<PaymentResponse>> getPaymentInfo(@PathVariable Long orderId) {
		var payments = paymentService.getPayment(orderId);

		return ApiResult.ok(payments);
	}
}
