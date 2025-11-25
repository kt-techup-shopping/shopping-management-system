package com.shop.domain.cart.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.cart.model.Cart;
import com.shop.domain.cart.repository.CartRepository;
import com.shop.domain.cart.response.CartResponse;
import com.shop.domain.cartitem.model.CartItem;
import com.shop.domain.cartitem.repository.CartItemRepository;
import com.shop.domain.cartitem.request.CartItemRequest;
import com.shop.domain.cartitem.response.CartItemResponse;
import com.shop.domain.product.model.Product;
import com.shop.domain.product.repository.ProductRepository;
import com.shop.domain.user.model.User;
import com.shop.domain.user.repository.UserRepository;
import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;
import com.shop.global.common.Preconditions;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	public CartResponse getCart(Long userId) {
		return cartRepository.findWithCartItemsAndProductsByUserId(userId)
			.map(cart -> {
				cart.removeInactiveProducts(); // 비활성 상태 상품 자동 제거
				return CartResponse.from(cart);
			})
			.orElse(CartResponse.empty(userId));
	}

	public Page<CartItemResponse> searchCartItems(Long userId, String keyword, Pageable pageable) {
		return cartItemRepository.search(userId, keyword, pageable);
	}

	@Transactional
	public Long addCartItem(Long userId, Long productId, CartItemRequest.Create request) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));
		Preconditions.validate(product.getStock() >= request.getQuantity(), ErrorCode.NOT_ENOUGH_STOCK);

		Cart cart = getOrCreateCart(userId);

		return cartItemRepository.findWithProductByCartUserIdAndProductId(userId, request.getProductId())
			.map(existingItem -> {
				Long newQuantity = existingItem.getQuantity() + request.getQuantity();
				Preconditions.validate(product.getStock() >= newQuantity, ErrorCode.NOT_ENOUGH_STOCK);
				existingItem.addQuantity(request.getQuantity());
				return existingItem.getId();
			})
			.orElseGet(() -> {
				CartItem cartItem = new CartItem(request.getQuantity(), cart, product);
				return cartItemRepository.save(cartItem).getId();
			});
	}

	@Transactional
	public void updateCartItem(Long userId, Long cartItemId, CartItemRequest.Update request) {
		CartItem cartItem = cartItemRepository.findWithProductByCartUserIdAndId(userId, cartItemId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

		Product product = cartItem.getProduct();

		// 상품의 상태 확인, 활성이 아닐시 오류 발생
		Preconditions.validate(product.isActive(), ErrorCode.NOT_ACTIVE);
		// 상품의 재고가 담으려는 재고보다 적을 경우 오류 발생
		Preconditions.validate(product.getStock() >= request.getQuantity(), ErrorCode.NOT_ENOUGH_STOCK);

		cartItem.updateQuantity(request.getQuantity());
	}

	@Transactional
	public void deleteCartItem(Long userId, Long cartItemId) {
		CartItem cartItem = cartItemRepository.findByCartUserIdAndId(userId, cartItemId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

		cartItemRepository.delete(cartItem);
	}

	@Transactional
	public void deleteCartItems(Long userId, CartItemRequest.Delete request) {
		cartItemRepository.deleteByCartUserIdAndIdIn(userId, request.getCartItemId());
	}

	@Transactional
	public void clearCart(Long userId) {
		cartItemRepository.deleteAllByCartUserId(userId);
	}

	@Transactional // TODO.. 모든 장바구니에서 해당 상품을 없애는 관리자 기능이지만 미구현
	public void removeProductFromAllCarts(Long productId) {
		cartItemRepository.deleteByProductId(productId);
	}

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