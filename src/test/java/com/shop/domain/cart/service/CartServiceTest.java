package com.shop.domain.cart.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.shop.domain.cart.model.Cart;
import com.shop.domain.cart.repository.CartRepository;
import com.shop.domain.cart.response.CartResponse;
import com.shop.domain.cartitem.model.CartItem;
import com.shop.domain.cartitem.repository.CartItemRepository;
import com.shop.domain.cartitem.request.CartItemCreate;
import com.shop.domain.cartitem.request.CartItemDelete;
import com.shop.domain.cartitem.response.CartItemResponse;
import com.shop.domain.product.model.Product;
import com.shop.domain.product.repository.ProductRepository;
import com.shop.domain.user.model.Gender;
import com.shop.domain.user.model.Role;
import com.shop.domain.user.model.Status;
import com.shop.domain.user.model.User;
import com.shop.domain.user.repository.UserRepository;
import com.shop.global.common.CustomException;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

	@InjectMocks
	private CartService cartService;

	@Mock
	private CartRepository cartRepository;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CartItemRepository cartItemRepository;

	private User user;
	private Cart cart;
	private Product product;
	private CartItem cartItem;

	@BeforeEach
	void setUp() {
		user = new User("QWER", UUID.randomUUID(), "Qwer!234",
			"QWER", "test@example.com", "010-1234-4567",
			Gender.MALE, LocalDate.now(), Role.USER, Status.ACTIVE);
		cart = new Cart(user);
		product = new Product("테스트상품1", 10000L, 100L);
		cartItem = new CartItem(5L, cart, product);

		ReflectionTestUtils.setField(user, "id", 1L);
		ReflectionTestUtils.setField(cart, "id", 1L);
		ReflectionTestUtils.setField(product, "id", 1L);
		ReflectionTestUtils.setField(cartItem, "id", 1L);
	}

	@Test
	void 장바구니_조회_성공() {
		//given
		given(cartRepository.findWithCartItemsAndProductsByUserId(user.getId()))
			.willReturn(Optional.of(cart));

		//when
		CartResponse result = cartService.getCart(user.getId());

		//then
		assertThat(result).isNotNull();
	}

	@Test
	void 빈_장바구니_조회_성공() {
		//given
		given(cartRepository.findWithCartItemsAndProductsByUserId(user.getId()))
			.willReturn(Optional.empty());

		//when
		CartResponse result = cartService.getCart(user.getId());

		//then
		assertThat(result).isNotNull();
	}

	@Test
	void 장바구니_상품_검색_성공() {
		//given
		String keyword = "테스트";

		var pageable = PageRequest.of(0, 10);
		var response = CartItemResponse.of(cartItem);

		Page<CartItemResponse> expectedPage = new PageImpl<>(List.of(response));

		given(cartItemRepository.search(user.getId(), keyword, pageable))
			.willReturn(expectedPage);

		//when
		Page<CartItemResponse> result = cartService.searchCartItems(user.getId(), keyword, pageable);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
	}

	@Test
	void 장바구니에_기존_상품_수량_증가() {
		// spy를 사용하여 수량을 체크할 수 있음
		//given
		var request = new CartItemCreate(product.getId(), 3L);

		CartItem spyCartItem = spy(cartItem);

		given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
		given(cartRepository.findByUserId(user.getId())).willReturn(Optional.of(cart));
		given(cartItemRepository.findWithProductByCartUserIdAndProductId(user.getId(), product.getId()))
			.willReturn(Optional.of(spyCartItem));

		//when
		cartService.addCartItem(user.getId(), product.getId(), request);

		//then
		verify(spyCartItem).addQuantity(3L);
		System.out.println(spyCartItem.getQuantity());
	}

	@Test
	void 장바구니에_새_상품_추가_성공() {
		// 해당 테스트 코드를 해결 하지 못하여 ai에게 도움을 받음
		//given
		Product product1 = new Product("테스트상품2", 5000L, 50L);
		ReflectionTestUtils.setField(product1, "id", 2L);
		var request = new CartItemCreate(product1.getId(), 3L);

		given(productRepository.findById(product1.getId())).willReturn(Optional.of(product1));
		given(cartRepository.findByUserId(user.getId())).willReturn(Optional.of(cart));
		given(cartItemRepository.findWithProductByCartUserIdAndProductId(user.getId(), product1.getId()))
			.willReturn(Optional.empty()); // 새 상품
		given(cartItemRepository.save(any(CartItem.class))).willReturn(cartItem);

		//when
		cartService.addCartItem(user.getId(), product1.getId(), request);

		//then
		verify(cartItemRepository).save(any(CartItem.class)); // 새 상품 추가됨
		verify(cartItemRepository, times(1)).save(any(CartItem.class));

		// 확인용 메서드 아래의 메서드를 넣으면 카트리스폰스를 확인가능
		// given(cartRepository.findWithCartItemsAndProductsByUserId(user.getId()))
		// 	.willReturn(Optional.of(cart));
		// CartResponse result = cartService.getCart(user.getId());
		// System.out.println(result);
		// assertThat(result.isEmpty()).isFalse();
	}

	@Test
	void 장바구니_상품_추가_실패_재고_부족() {
		//given
		var request = new CartItemCreate(product.getId(), 500L);

		given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

		//then
		assertThrowsExactly(CustomException.class, () ->
			cartService.addCartItem(user.getId(), product.getId(), request)
		);
	}

	@Test
	void 장바구니_상품_추가_실패_없는_상품() {
		//given
		Long productId = 99999L;

		var request = new CartItemCreate(productId, 3L);

		given(productRepository.findById(productId)).willReturn(Optional.of(product));

		//then
		assertThrowsExactly(CustomException.class, () ->
			cartService.addCartItem(user.getId(), productId, request)
		);
	}

	@Test
	void 장바구니_상품_삭제_성공() {
		//given
		given(cartItemRepository.findByCartUserIdAndId(user.getId(), cartItem.getId()))
			.willReturn(Optional.of(cartItem));

		//when
		cartService.deleteCartItem(user.getId(), cartItem.getId());

		//then
		verify(cartItemRepository).delete(cartItem);
	}

	@Test
	void 장바구니_상품_다중_삭제_성공() {
		//given
		List<Long> cartItemIds = Arrays.asList(1L, 2L, 3L);
		var request = new CartItemDelete(cartItemIds);

		//when
		cartService.deleteCartItems(user.getId(), request);

		//then
		verify(cartItemRepository).deleteByCartUserIdAndIdIn(user.getId(), cartItemIds);
	}

	@Test
	void 장바구니_비우기_성공() {
		//when
		cartService.clearCart(user.getId());

		//then
		verify(cartItemRepository).deleteAllByCartUserId(user.getId());
	}
}