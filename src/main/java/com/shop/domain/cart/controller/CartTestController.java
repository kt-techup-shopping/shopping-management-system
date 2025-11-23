package com.shop.domain.cart.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.cart.response.CartResponse;
import com.shop.domain.cart.service.CartService;
import com.shop.domain.cartitem.request.CartItemRequest;
import com.shop.domain.cartitem.response.CartItemResponse;
import com.shop.global.common.ApiResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "장바구니", description = "장바구니 API")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartTestController {

	private final CartService cartService;

	// 내 장바구니 조회
	@Operation(summary = "장바구니 조회", description = "특정 사용자의 장바구니를 조회합니다.")
	@GetMapping("/{userId}")
	public ApiResult<CartResponse.Detail> getCart(
		@Parameter(description = "사용자 ID") @PathVariable Long userId) {
		CartResponse.Detail cart = cartService.getCart(userId);
		return ApiResult.ok(cart);
	}

	// 장바구니 검색
	@Operation(summary = "장바구니 검색", description = "장바구니 내 상품을 검색합니다.")
	@GetMapping("/{userId}/search")
	public ApiResult<Page<CartItemResponse.Detail>> searchCartItems(
		@Parameter(description = "사용자 ID") @PathVariable Long userId,
		@RequestParam(required = false) String keyword,
		Pageable pageable) {
		Page<CartItemResponse.Detail> result = cartService.searchCartItems(userId, keyword, pageable);
		return ApiResult.ok(result);
	}

	// 장바구니 추가
	@Operation(summary = "장바구니 상품 추가", description = "장바구니에 상품을 추가합니다.")
	@PostMapping("/{userId}/items")
	public ApiResult<Long> addCartItem(
		@Parameter(description = "사용자 ID") @PathVariable Long userId,
		@Valid @RequestBody CartItemRequest.Create request) {
		Long cartItemId = cartService.addCartItem(userId, request.getProductId(), request);
		return ApiResult.ok(cartItemId);
	}

	// 수량 변경
	@Operation(summary = "상품 수량 변경", description = "장바구니 상품의 수량을 변경합니다.")
	@PutMapping("/{userId}/items/{itemId}")
	public ApiResult<Void> updateCartItem(
		@Parameter(description = "사용자 ID") @PathVariable Long userId,
		@Parameter(description = "장바구니 아이템 ID") @PathVariable Long itemId,
		@Valid @RequestBody CartItemRequest.Update request) {
		cartService.updateCartItem(userId, itemId, request);
		return ApiResult.ok();
	}

	// 특정 아이템 삭제
	@Operation(summary = "특정 상품 삭제", description = "장바구니에서 특정 상품을 삭제합니다.")
	@PutMapping("/{userId}/items/{itemId}/delete")
	public ApiResult<Void> deleteCartItem(
		@Parameter(description = "사용자 ID") @PathVariable Long userId,
		@Parameter(description = "장바구니 아이템 ID") @PathVariable Long itemId) {
		cartService.deleteCartItem(userId, itemId);
		return ApiResult.ok();
	}

	// 전체 비우기
	@Operation(summary = "장바구니 비우기", description = "장바구니를 완전히 비웁니다.")
	@PutMapping("/{userId}")
	public ApiResult<Void> clearCart(
		@Parameter(description = "사용자 ID") @PathVariable Long userId) {
		cartService.clearCart(userId);
		return ApiResult.ok();
	}
}