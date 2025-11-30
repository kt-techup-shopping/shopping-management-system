package com.shop.domain.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.order.repository.OrderRepositoryCustom;
import com.shop.domain.order.response.AdminOrderDetailResponse;
import com.shop.domain.order.response.AdminOrderDetailUserResponse;
import com.shop.domain.order.service.AdminOrderService;
import com.shop.global.common.ApiResult;
import com.shop.global.common.Paging;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
@Tag(name = "관리자 주문 API")
public class AdminOrderController {
	private final OrderRepositoryCustom orderRepository;
	private final AdminOrderService adminOrderService;

	// 여기서 서비스에서 하는게 없음
	// 1. 리포지토리 주입 바로 받아서 할거냐 -> 싱크홀 안티패턴 (v)
	// 2. 그래도~ 서비스를 통해야한다.
	// @GetMapping
	// public ApiResult<Page<OrderResponse.Search>> search(
	// 	@RequestParam(required = false) String keyword,
	// 	@Parameter(hidden = true) Paging paging
	// ) {
	// 	return ApiResult.ok(orderRepository.search(keyword, paging.toPageable()));
	// }

	@GetMapping
	@Operation(summary = "관리자 주문 목록 조회", description = "관리자가 주문 ID, 사용자 ID, 주문 상태를 조건으로 주문 목록을 조회합니다.")
	public ApiResult<Page<AdminOrderDetailResponse>> getOrders(
		@RequestParam(required = false) Long orderId,
		@RequestParam(required = false) Long userId,
		@RequestParam(required = false) String status,
		@Parameter Paging paging
	) {
		return ApiResult.ok(adminOrderService.getOrders(orderId, userId, status, paging));
	}

	@GetMapping("/{id}/detail")
	@Operation(summary = "관리자 주문 상세 조회", description = "관리자가 특정 주문 ID의 상세 정보를 조회합니다.")
	public ApiResult<AdminOrderDetailUserResponse> getAdminOrderDetailById(@PathVariable Long id) {
		return ApiResult.ok(adminOrderService.getAdminOrderDetailById(id));
	}
}
