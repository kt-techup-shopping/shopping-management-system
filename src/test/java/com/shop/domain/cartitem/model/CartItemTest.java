package com.shop.domain.cartitem.model;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.shop.domain.cart.model.Cart;
import com.shop.domain.product.model.Product;
import com.shop.domain.user.model.Gender;
import com.shop.domain.user.model.Role;
import com.shop.domain.user.model.Status;
import com.shop.domain.user.model.User;
import com.shop.global.common.CustomException;

class CartItemTest {

	@Test
	void 카트아이템_생성_성공() {
		var user = new User("QWER", UUID.randomUUID(), "Qwer!234", "QWER",
			"test@example.com", "010-1234-4567", Gender.MALE,
			LocalDate.now(), Role.USER, Status.ACTIVE);
		var cart = new Cart(user);
		var product = new Product("테스트상품", 10000L, 10L);

		var cartItem = new CartItem(5L, cart, product);

		assertThat(cartItem.getQuantity()).isEqualTo(5L);
		assertThat(cartItem.getCart()).isEqualTo(cart);
		assertThat(cartItem.getProduct()).isEqualTo(product);
	}

	@ParameterizedTest
	@ValueSource(longs = {0L, -1L, -10L})
	void 카트아이템_생성_실패_수량이_0_이하(Long quantity) {
		var user = new User("QWER", UUID.randomUUID(), "Qwer!234", "QWER",
			"test@example.com", "010-1234-4567", Gender.MALE,
			LocalDate.now(), Role.USER, Status.ACTIVE);
		var cart = new Cart(user);
		var product = new Product("테스트상품", 10000L, 10L);

		assertThrowsExactly(CustomException.class, () ->
			new CartItem(quantity, cart, product)
		);
	}

}
