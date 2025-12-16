package com.shop.cart.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.CustomException;
import com.shop.ErrorCode;
import com.shop.Preconditions;
import com.shop.cart.response.CartResponse;
import com.shop.cartitem.request.CartItemCreate;
import com.shop.cartitem.request.CartItemDelete;
import com.shop.cartitem.request.CartItemUpdate;
import com.shop.cartitem.response.CartItemResponse;
import com.shop.domain.cart.Cart;
import com.shop.domain.cartItem.CartItem;
import com.shop.domain.product.Product;
import com.shop.domain.user.User;
import com.shop.repository.cart.CartRepository;
import com.shop.repository.cartItem.CartItemRepository;
import com.shop.repository.cartItem.response.CartItemQueryResponse;
import com.shop.repository.product.ProductRepository;
import com.shop.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	// 장바구니를 조회하는 API
	public CartResponse getCart(Long userId) {
		return cartRepository.findWithCartItemsAndProductsByUserId(userId)
			.map(cart -> {
				cart.removeInactiveProducts(); // 비활성 상태 상품 자동 제거
				return CartResponse.from(cart);
			})
			.orElse(CartResponse.empty(userId));
	}

	// 장바구니를 검색하는 API
	public Page<CartItemResponse> searchCartItems(Long userId, String keyword, Pageable pageable) {
		Page<CartItemQueryResponse> queryResponses = cartItemRepository.search(userId, keyword, pageable);
		return queryResponses.map(queryResponse ->
			CartItemResponse.of(queryResponse.cartItem())
		);
	}
	/*
	public Page<CartItemResponse> searchCartItems(Long userId, String keyword, Pageable pageable) {
		Page<CartItemQueryResponse> queryResponses = cartItemRepository.search(userId, keyword, pageable);
		return queryResponses.map(it -> new CartItemResponse(
				it.cartItemId(),
				it.productId(),
				it.productName(),
				it.description(),
				it.color(),
				it.categories().getName(),
				it.productPrice(),
				it.discountPrice(),
				it.quantity(),
				it.totalPrice(),
				it.totalDiscountPrice(),
				it.isAvailable()
			)
		);
	} */

	// 장바구니에 상품을 담는 API
	@Transactional
	public Long addCartItem(Long userId, Long productId, CartItemCreate request) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));
		Preconditions.validate(product.getStock() >= request.quantity(), ErrorCode.NOT_ENOUGH_STOCK);

		Cart cart = getOrCreateCart(userId);

		return cartItemRepository.findWithProductByCartUserIdAndProductId(userId, request.productId())
			.map(existingItem -> {
				Long newQuantity = existingItem.getQuantity() + request.quantity();
				Preconditions.validate(product.getStock() >= newQuantity, ErrorCode.NOT_ENOUGH_STOCK);
				existingItem.addQuantity(request.quantity());
				return existingItem.getId();
			})
			.orElseGet(() -> {
				CartItem cartItem = new CartItem(request.quantity(), cart, product);
				return cartItemRepository.save(cartItem).getId();
			});
	}

	// 장바구니 상품의 수량을 변경하는 API
	@Transactional
	public void updateCartItem(Long userId, Long cartItemId, CartItemUpdate request) {
		CartItem cartItem = cartItemRepository.findWithProductByCartUserIdAndId(userId, cartItemId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

		Product product = cartItem.getProduct();

		// 상품의 상태 확인, 활성이 아닐시 오류 발생
		Preconditions.validate(product.isActive(), ErrorCode.NOT_ACTIVE);
		// 상품의 재고가 담으려는 재고보다 적을 경우 오류 발생
		Preconditions.validate(product.getStock() >= request.quantity(), ErrorCode.NOT_ENOUGH_STOCK);

		cartItem.updateQuantity(request.quantity());
	}

	// 장바구니에서 상품을 삭제하는 API
	@Transactional
	public void deleteCartItem(Long userId, Long cartItemId) {
		CartItem cartItem = cartItemRepository.findByCartUserIdAndId(userId, cartItemId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

		cartItemRepository.delete(cartItem);
	}

	// 장바구니에서 지정한 상품들을 삭제하는 API
	@Transactional
	public void deleteCartItems(Long userId, CartItemDelete request) {
		cartItemRepository.deleteByCartUserIdAndIdIn(userId, request.cartItemId());
	}

	// 장바구니를 비우는 API
	@Transactional
	public void clearCart(Long userId) {
		cartItemRepository.deleteAllByCartUserId(userId);
	}

	// 장바구니를 생성하는 메서드
	private Cart getOrCreateCart(Long userId) {
		return cartRepository.findByUserId(userId)
			.orElseGet(() -> {
				User user = userRepository.findById(userId)
					.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
				Cart cart = new Cart(user);
				return cartRepository.save(cart);
			});
	}

}