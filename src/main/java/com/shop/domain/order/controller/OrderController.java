package com.shop.domain.order.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.order.request.OrderCreateRequest;
import com.shop.domain.order.request.OrderDeleteRequest;
import com.shop.domain.order.request.OrderUpdateRequest;
import com.shop.domain.order.response.OrderDetailResponse;
import com.shop.domain.order.response.OrderDetailUserResponse;
import com.shop.domain.order.service.OrderService;
import com.shop.global.common.ApiResult;
import com.shop.global.common.SwaggerAssistance;
import com.shop.global.security.DefaultCurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "주문 API")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController extends SwaggerAssistance {
	private final OrderService orderService;

	//주문생성
	@Operation(summary = "주문 생성", description = "사용자가 상품을 선택하여 주문을 생성합니다.")
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
	@GetMapping("/{id}/detail")
	public ApiResult<OrderDetailUserResponse> getMyOrderDetail(
		@AuthenticationPrincipal DefaultCurrentUser defaultCurrentUser,
		@PathVariable Long id
	) {
		return ApiResult.ok(orderService.getMyOrderDetail(defaultCurrentUser.getId(), id));
	}
}
