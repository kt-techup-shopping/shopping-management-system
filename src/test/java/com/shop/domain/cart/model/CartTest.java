package com.shop.domain.cart.model;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.shop.domain.cartitem.model.CartItem;
import com.shop.domain.product.model.Product;
import com.shop.domain.user.model.Gender;
import com.shop.domain.user.model.Role;
import com.shop.domain.user.model.Status;
import com.shop.domain.user.model.User;

class CartTest {

	private Cart cart;
	private User user;

	@BeforeEach
	void setUp() {
		user = new User("QWER", UUID.randomUUID(), "Qwer!234",
			"QWER", "test@example.com", "010-1234-4567",
			Gender.MALE, LocalDate.now(), Role.USER, Status.ACTIVE);

		cart = new Cart(user);
	}

	@Test
	void 카트_생성_성공() {

		//then
		assertThat(cart.getUser()).isEqualTo(user);
		assertThat(cart.getCartItems()).isEmpty();
	}

	@Test
	void 카트_추가() {
		//given
		var product = new Product("테스트상품", 10000L, 100L);
		var cartItem = new CartItem(5L, cart, product);

		//when
		cart.addCartItem(cartItem);

		//then
		assertThat(cart.getCartItems()).hasSize(2);
		//CartItem의 setCart 메서드로 인하여 기본적으로 추가되는 것이 있어서 사이즈가 2
		assertThat(cart.getCartItems()).contains(cartItem);
	}

	@Test
	void 카트_상품_제거() {
		//given
		var product = new Product("테스트상품", 10000L, 100L);
		var cartItem = new CartItem(5L, cart, product);

		//when
		cart.removeCartItem(cartItem);

		//then
		assertThat(cart.getCartItems()).isEmpty();
	}

	@Test
	void 전체_가격_계산() {
		//given
		var product1 = new Product("테스트상품1", 10000L, 100L);
		var product2 = new Product("테스트상품2", 5000L, 50L);

		var cartItem1 = new CartItem(2L, cart, product1);
		var cartItem2 = new CartItem(3L, cart, product2);

		//when
		Long totalPrice = cart.getTotalPrice();

		//then
		assertThat(totalPrice).isEqualTo(35000L);
	}

	@Test
	void 전체_상품_개수_계산() {
		//given
		var product1 = new Product("상품1", 10000L, 100L);
		var product2 = new Product("상품2", 5000L, 50L);

		var cartItem1 = new CartItem(2L, cart, product1);
		var cartItem2 = new CartItem(3L, cart, product2);

		//when
		int totalCount = cart.getTotalItemCount();

		//then
		assertThat(totalCount).isEqualTo(5);
	}

	@Test
	void 카트_비우기() {
		//given
		var product = new Product("테스트상품", 10000L, 100L);
		var cartItem = new CartItem(3L, cart, product);

		//when
		cart.addCartItem(cartItem);

		cart.clearCartItems();

		//then
		assertThat(cart.getCartItems()).isEmpty();
	}
}