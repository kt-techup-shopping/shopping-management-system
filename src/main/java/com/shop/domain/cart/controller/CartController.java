package com.shop.domain.cart.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.shop.global.security.CurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "장바구니", description = "장바구니 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;

	@Operation(summary = "장바구니 조회")
	@GetMapping("/cart")
	public ApiResult<CartResponse> getCart(
		@AuthenticationPrincipal CurrentUser currentUser) {
		CartResponse cart = cartService.getCart(currentUser.getId());
		return ApiResult.ok(cart);
	}

	@Operation(summary = "장바구니 검색")
	@GetMapping("/cart/search")
	public ApiResult<Page<CartItemResponse>> searchCartItems(
		@AuthenticationPrincipal CurrentUser currentUser,
		@RequestParam(required = false) String keyword,
		Pageable pageable) {
		Page<CartItemResponse> result = cartService.searchCartItems(
			currentUser.getId(), keyword, pageable);
		return ApiResult.ok(result);
	}

	@Operation(summary = "장바구니 상품 추가")
	@PostMapping("/cart/items")
	public ApiResult<Long> addCartItem(
		@AuthenticationPrincipal CurrentUser currentUser,
		@Valid @RequestBody CartItemRequest.Create request) {
		Long cartItemId = cartService.addCartItem(currentUser.getId(), request.getProductId(), request);
		return ApiResult.ok(cartItemId);
	}

	@Operation(summary = "상품 수량 변경")
	@PutMapping("/cart/items/{itemId}")
	public ApiResult<Void> updateCartItem(
		@AuthenticationPrincipal CurrentUser currentUser,
		@PathVariable Long itemId,
		@Valid @RequestBody CartItemRequest.Update request) {
		cartService.updateCartItem(currentUser.getId(), itemId, request);
		return ApiResult.ok();
	}

	@Operation(summary = "특정 상품 삭제")
	@PutMapping("/cart/items/{itemId}/delete")
	public ApiResult<Void> deleteCartItem(
		@AuthenticationPrincipal CurrentUser currentUser,
		@PathVariable Long itemId) {
		cartService.deleteCartItem(currentUser.getId(), itemId);
		return ApiResult.ok();
	}

	@Operation(summary = "선택 상품 일괄 삭제")
	@PutMapping("/cart/items")
	public ApiResult<Void> deleteCartItems(
		@AuthenticationPrincipal CurrentUser currentUser,
		@Valid @RequestBody CartItemRequest.Delete request) {
		cartService.deleteCartItems(currentUser.getId(), request);
		return ApiResult.ok();
	}

	@Operation(summary = "장바구니 비우기")
	@PutMapping("/cart")
	public ApiResult<Void> clearCart(
		@AuthenticationPrincipal CurrentUser currentUser) {
		cartService.clearCart(currentUser.getId());
		return ApiResult.ok();
	}
}