package com.shop.cart.controller;

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

import com.shop.ApiResult;
import com.shop.ErrorCode;
import com.shop.Paging;
import com.shop.cart.response.CartResponse;
import com.shop.cart.service.CartService;
import com.shop.cartitem.request.CartItemCreate;
import com.shop.cartitem.request.CartItemDelete;
import com.shop.cartitem.request.CartItemUpdate;
import com.shop.cartitem.response.CartItemResponse;
import com.shop.docs.ApiErrorCodeExamples;
import com.shop.security.CurrentUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@Tag(name = "장바구니", description = "장바구니 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

	private final CartService cartService;

	@Operation(summary = "장바구니 조회", description = "장바구니 조회 API, 장바구니를 확인하는 기능")
	@GetMapping
	public ApiResult<CartResponse> getCart(
		@AuthenticationPrincipal CurrentUser currentUser) {
		CartResponse cart = cartService.getCart(currentUser.getId());
		return ApiResult.ok(cart);
	} // ex : GET http://localhost:8080/cart

	@Operation(summary = "장바구니 검색", description = "장바구니 검색 API, 장바구니에 담긴 물건을 검색하는 기능")
	@GetMapping("/search")
	public ApiResult<Page<CartItemResponse>> searchCartItems(
		@AuthenticationPrincipal CurrentUser currentUser,
		@RequestParam(required = false) @NotBlank String keyword,
		@Parameter(hidden = true) Paging paging
	) {
		Page<CartItemResponse> result = cartService.searchCartItems(
			currentUser.getId(), keyword, paging.toPageable());
		return ApiResult.ok(result);
	} // ex : GET http://localhost:8080/cart/search?keyword="keyword"

	@Operation(summary = "장바구니 상품 추가", description = "장바구니 상품 추가 API, 장바구니에 상품을 추가하면 추가하는 숫자만큼 늘어남 = 줄일 수 없고 늘어나기만 함")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_FOUND_PRODUCT,
		ErrorCode.NOT_ENOUGH_STOCK,
	})
	@PostMapping("/items")
	public ApiResult<Long> addCartItem(
		@AuthenticationPrincipal CurrentUser currentUser,
		@Valid @RequestBody CartItemCreate request) {
		Long cartItemId = cartService.addCartItem(currentUser.getId(), request.productId(), request);
		return ApiResult.ok(cartItemId);
	} // ex : POST http://localhost:8080/cart/items {"productId" : 1, "quantity" : 2}

	@Operation(summary = "상품 수량 변경", description = "장바구니 상품 수량 변경 API, 장바구니 내의 상품 수량을 변경하는 기능")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_FOUND_PRODUCT,
		ErrorCode.NOT_ACTIVE,
		ErrorCode.NOT_ENOUGH_STOCK,
	})
	@PutMapping("/items/{itemId}")
	public ApiResult<Void> updateCartItem(
		@AuthenticationPrincipal CurrentUser currentUser,
		@PathVariable @Min(1) Long itemId,
		@Valid @RequestBody CartItemUpdate request) {
		cartService.updateCartItem(currentUser.getId(), itemId, request);
		return ApiResult.ok();
	} // ex : PUT http://localhost:8080/cart/items/1 /{"quantity" : 3}

	@Operation(summary = "특정 상품 삭제", description = "장바구니 상품 삭제 API, 장바구니 내의 특정 상품만 삭제하는 기능 (단건)")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_FOUND_PRODUCT
	})
	@PutMapping("/items/{itemId}/delete")
	public ApiResult<Void> deleteCartItem(
		@AuthenticationPrincipal CurrentUser currentUser,
		@PathVariable @Min(1) Long itemId) {
		cartService.deleteCartItem(currentUser.getId(), itemId);
		return ApiResult.ok();
	} // ex : PUT http://localhost:8080/cart/3/delete

	@Operation(summary = "선택 상품 일괄 삭제", description = "장바구니 상품 삭제 API, 장바구니 내의 상품을 여러개 삭제하는 기능")
	@PutMapping("/items")
	public ApiResult<Void> deleteCartItems(
		@AuthenticationPrincipal CurrentUser currentUser,
		@Valid @RequestBody CartItemDelete request) {
		cartService.deleteCartItems(currentUser.getId(), request);
		return ApiResult.ok();
	} // ex : PUT http://localhost:8080/cart/items {"cartItemId" : [1,2]}

	@Operation(summary = "장바구니 비우기", description = "장바구니 전체 삭제 API, 장바구니의 모든 상품을 없애는 기능")
	@PutMapping
	public ApiResult<Void> clearCart(
		@AuthenticationPrincipal CurrentUser currentUser) {
		cartService.clearCart(currentUser.getId());
		return ApiResult.ok();
	} // ex : PUT http://localhost:8080/cart/

}