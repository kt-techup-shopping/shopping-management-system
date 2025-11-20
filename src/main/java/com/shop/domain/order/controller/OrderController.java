package com.shop.domain.order.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.order.service.OrderService;
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

	//주문생성
	@PostMapping
	public ApiResult<Void> create(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@RequestBody @Valid OrderRequest.Create request) {
		orderService.create(
			defaultCurrentUser.getId(),
			request.productId(),
			request.receiverName(),
			request.receiverAddress(),
			request.receiverMobile(),
			request.quantity()
		);
		return ApiResult.ok();
	}
}
