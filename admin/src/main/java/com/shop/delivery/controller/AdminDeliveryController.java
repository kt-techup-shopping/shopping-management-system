package com.shop.delivery.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.ApiResult;
import com.shop.ErrorCode;
import com.shop.delivery.request.DeliveryReadyRequest;
import com.shop.delivery.response.DeliveryResponse;
import com.shop.delivery.service.AdminDeliveryService;
import com.shop.docs.ApiErrorCodeExamples;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@Tag(name = "관리자 배송 상태 변경", description = "관리자가 배송 상태를 변경하는 API")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/orders")
public class AdminDeliveryController {
	private final AdminDeliveryService adminDeliveryService;

	@Operation(summary = "주문 확인 상태로 변경", description = "Pending 상태로 변경하는 API")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_FOUND_ORDER,
		ErrorCode.NOT_FOUND_DELIVERY,
	})
	@PutMapping("/{orderId}/delivery-status/pending")
	public ApiResult<DeliveryResponse> updateToPending(
		@PathVariable @Min(1) Long orderId) {
		DeliveryResponse response = adminDeliveryService.updateToPending(orderId);
		return ApiResult.ok(response);
	}

	@Operation(summary = "배송 준비 상태로 변경", description = "READY 상태로 변경하는 API, 송장을 이 단계에서 등록하여야함")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_FOUND_ORDER,
		ErrorCode.NOT_FOUND_DELIVERY,
	})
	@PutMapping("/{orderId}/delivery-status/ready")
	public ApiResult<DeliveryResponse> updateToReady(
		@PathVariable Long orderId,
		@Valid @RequestBody DeliveryReadyRequest request) {
		DeliveryResponse response = adminDeliveryService.updateToReady(orderId, request);
		return ApiResult.ok(response);
	}

	@Operation(summary = "주문 확인 상태로 변경", description = "SHIPPING 상태로 변경하는 API")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_FOUND_ORDER,
		ErrorCode.NOT_FOUND_DELIVERY,
	})
	@PutMapping("/{orderId}/delivery-status/shipping")
	public ApiResult<DeliveryResponse> updateToShipping(
		@PathVariable @Min(1) Long orderId) {
		DeliveryResponse response = adminDeliveryService.updateToShipping(orderId);
		return ApiResult.ok(response);
	}

	@Operation(summary = "주문 확인 상태로 변경", description = "DELIVERED 상태로 변경하는 API")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_FOUND_ORDER,
		ErrorCode.NOT_FOUND_DELIVERY,
	})
	@PutMapping("/{orderId}/delivery-status/delivered")
	public ApiResult<DeliveryResponse> updateToDelivered(
		@PathVariable @Min(1) Long orderId) {
		DeliveryResponse response = adminDeliveryService.updateToDelivered(orderId);
		return ApiResult.ok(response);
	}
}
