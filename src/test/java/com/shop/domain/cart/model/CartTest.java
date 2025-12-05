package com.shop.domain.cart.model;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.shop.domain.cartitem.model.CartItem;
import com.shop.domain.product.model.Product;
import com.shop.domain.user.model.Gender;
import com.shop.domain.user.model.Role;
import com.shop.domain.user.model.Status;
import com.shop.domain.user.model.User;

class CartTest {
	@Test
	void 카트_생성_성공() {
		var user = new User("QWER", UUID.randomUUID(), "Qwer!234",
			"QWER", "test@example.com", "010-1234-4567",
			Gender.MALE, LocalDate.now(), Role.USER, Status.ACTIVE);

		var cart = new Cart(user);

		assertThat(cart.getUser()).isEqualTo(user);
		assertThat(cart.getCartItems()).isEmpty();
	}

	@Test
	void 카트_추가() {
		var user = new User("QWER", UUID.randomUUID(), "Qwer!234",
			"QWER", "test@example.com", "010-1234-4567",
			Gender.MALE, LocalDate.now(), Role.USER, Status.ACTIVE);
		var cart = new Cart(user);
		var product = new Product("테스트상품", 10000L, 100L);
		var cartItem = new CartItem(5L, cart, product);

		cart.addCartItem(cartItem);

		assertThat(cart.getCartItems()).hasSize(2);
		//CartItem의 setCart 메서드로 인하여 기본적으로 추가되는 것이 있어서 사이즈가 2
		assertThat(cart.getCartItems()).contains(cartItem);
	}

	@Test
	void 카트_상품_제거() {
		var user = new User("QWER", UUID.randomUUID(), "Qwer!234",
			"QWER", "test@example.com", "010-1234-4567",
			Gender.MALE, LocalDate.now(), Role.USER, Status.ACTIVE);
		var cart = new Cart(user);
		var product = new Product("테스트상품", 10000L, 100L);
		var cartItem = new CartItem(5L, cart, product);

		cart.removeCartItem(cartItem);

		assertThat(cart.getCartItems()).isEmpty();
	}
}