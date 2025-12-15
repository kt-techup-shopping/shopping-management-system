package com.kt.domain.cart;

import java.util.ArrayList;
import java.util.List;

import com.shop.domain.cartitem.model.CartItem;
import com.shop.domain.user.model.User;
import com.shop.global.common.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Cart extends BaseEntity {

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	@OneToMany(mappedBy = "cart")
	private List<CartItem> cartItems = new ArrayList<>();

	public Cart(User user) {
		this.user = user;
	}

	public void addCartItem(CartItem cartItem) {
		cartItems.add(cartItem);
	}

	public void removeCartItem(CartItem cartItem) {
		cartItems.remove(cartItem);
	}

	// 상품 전체 가격
	public Long getTotalPrice() {
		return cartItems.stream()
			.filter(CartItem::isAvailable)
			.mapToLong(CartItem::getTotalPrice)
			.sum();
	}

	// 할인된 가격
	public Long getTotalDiscountPrice() {
		return cartItems.stream()
			.filter(CartItem::isAvailable)
			.mapToLong(CartItem::getTotalDiscountPrice)
			.sum();
	}

	// 전체 상품 개수
	public int getTotalItemCount() {
		return cartItems.stream()
			.filter(CartItem::isAvailable)
			.mapToInt(item -> item.getQuantity().intValue())
			.sum();
	}

	// 카트 정리
	public void clearCartItems() {
		cartItems.clear();
	}

	// 비활성 상태의 상품 제거
	public void removeInactiveProducts() {
		cartItems.removeIf(item -> !item.getProduct().isActive());
	}
}
