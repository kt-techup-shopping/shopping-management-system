package com.shop.domain.cartitem.model;

import com.shop.domain.cart.model.Cart;
import com.shop.domain.product.model.Product;
import com.shop.global.common.BaseEntity;
import com.shop.global.common.ErrorCode;
import com.shop.global.common.Preconditions;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class CartItem extends BaseEntity {
	private Long quantity;

	@ManyToOne
	@JoinColumn(name = "cart_id")
	private Cart cart;
	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	public CartItem(Long quantity, Cart cart, Product product) {
		this.quantity = quantity;
		setCart(cart);
		this.product = product;
		Preconditions.validate(quantity >= 1, ErrorCode.MIN_PIECE);
	}

	public void setCart(Cart cart) {
		this.cart = cart;
		cart.addCartItem(this);
	}

	public void updateQuantity(Long quantity) {
		Preconditions.validate(quantity >= 1, ErrorCode.MIN_PIECE);
		this.quantity = quantity;
	}

	public void addQuantity(Long quantity) {
		Preconditions.validate(quantity >= 1, ErrorCode.MIN_PIECE);
		this.quantity += quantity;
	}

	public Long getTotalPrice() {
		return this.quantity * this.product.getPrice();
	}

	public Long getTotalDiscountPrice() {
		return this.quantity * this.product.getDiscountPrice();
	}

	public boolean isAvailable() {
		return product.isActive();
	}

	public boolean isSoldOut() {
		return product.isSoldOut();
	}
}
