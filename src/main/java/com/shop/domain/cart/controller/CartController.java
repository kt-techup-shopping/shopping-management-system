package com.shop.domain.cart.controller;

import org.springframework.data.domain.Page;
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
import com.shop.global.common.Paging;
import com.shop.global.security.CurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@Tag(name = "장바구니", description = "장바구니 API")
@RestController
@RequestMapping("/cart")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;

	// 장바구니 조회 API, 장바구니를 확인하는 기능
	@Operation(summary = "장바구니 조회")
	@GetMapping
	public ApiResult<CartResponse> getCart(
		@AuthenticationPrincipal CurrentUser currentUser) {
		CartResponse cart = cartService.getCart(currentUser.getId());
		return ApiResult.ok(cart);
	} // ex : GET http://localhost:8080/cart

	// 장바구니 검색 API, 장바구니에 담긴 물건을 검색하는 기능
	@Operation(summary = "장바구니 검색")
	@GetMapping("/search")
	public ApiResult<Page<CartItemResponse>> searchCartItems(
		@AuthenticationPrincipal CurrentUser currentUser,
		@RequestParam(required = false) String keyword,
		@Parameter(hidden = true) Paging paging
	) {
		Page<CartItemResponse> result = cartService.searchCartItems(
			currentUser.getId(), keyword, paging.toPageable());
		return ApiResult.ok(result);
	} // ex : GET http://localhost:8080/cart/search?keyword="keyword"

	// 장바구니 상품 추가 API, 장바구니에 상품을 추가하면 추가하는 숫자만큼 늘어남 = 줄일 수 없고 늘어나기만 함
	@Operation(summary = "장바구니 상품 추가")
	@PostMapping("/items")
	public ApiResult<Long> addCartItem(
		@AuthenticationPrincipal CurrentUser currentUser,
		@Valid @RequestBody CartItemRequest.Create request) {
		Long cartItemId = cartService.addCartItem(currentUser.getId(), request.getProductId(), request);
		return ApiResult.ok(cartItemId);
	} // ex : POST http://localhost:8080/cart/items {"productId" : 1, "quantity" : 2}

	// 장바구니 상품 수량 변경 API, 장바구니 내의 상품 수량을 변경하는 기능
	@Operation(summary = "상품 수량 변경")
	@PutMapping("/items/{itemId}")
	public ApiResult<Void> updateCartItem(
		@AuthenticationPrincipal CurrentUser currentUser,
		@PathVariable Long itemId,
		@Valid @RequestBody CartItemRequest.Update request) {
		cartService.updateCartItem(currentUser.getId(), itemId, request);
		return ApiResult.ok();
	} // ex : PUT http://localhost:8080/cart/items/1 /{"quantity" : 3}

	// 장바구니 상품 삭제 API, 장바구니 내의 특정 상품만 삭제하는 기능 (단건)
	@Operation(summary = "특정 상품 삭제")
	@PutMapping("/items/{itemId}/delete")
	public ApiResult<Void> deleteCartItem(
		@AuthenticationPrincipal CurrentUser currentUser,
		@PathVariable Long itemId) {
		cartService.deleteCartItem(currentUser.getId(), itemId);
		return ApiResult.ok();
	} // ex : PUT http://localhost:8080/cart/3/delete

	// 장바구니 상품 삭제 API, 장바구니 내의 상품을 여러개 삭제하는 기능
	@Operation(summary = "선택 상품 일괄 삭제")
	@PutMapping("/items")
	public ApiResult<Void> deleteCartItems(
		@AuthenticationPrincipal CurrentUser currentUser,
		@Valid @RequestBody CartItemRequest.Delete request) {
		cartService.deleteCartItems(currentUser.getId(), request);
		return ApiResult.ok();
	} // ex : PUT http://localhost:8080/cart/items {"cartItemId" : [1,2]}

	// 장바구니 전체 삭제 API, 장바구니의 모든 상품을 없애는 기능
	@Operation(summary = "장바구니 비우기")
	@PutMapping
	public ApiResult<Void> clearCart(
		@AuthenticationPrincipal CurrentUser currentUser) {
		cartService.clearCart(currentUser.getId());
		return ApiResult.ok();
	} // ex : PUT http://localhost:8080/cart/

}