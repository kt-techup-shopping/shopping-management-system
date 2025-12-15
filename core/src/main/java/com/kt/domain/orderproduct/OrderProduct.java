package com.kt.domain.orderproduct;

import java.util.ArrayList;
import java.util.List;

import com.shop.domain.order.model.Order;
import com.shop.domain.product.model.Product;
import com.shop.domain.review.model.Review;
import com.shop.global.common.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class OrderProduct extends BaseEntity {
	private Long quantity;

	@Version
	private Long version;

	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@OneToMany(mappedBy = "orderProduct")
	private List<Review> reviews = new ArrayList<>();

	public OrderProduct(Order order, Product product, Long quantity) {
		this.order = order;
		this.product = product;
		this.quantity = quantity;
	}

	// 주문 수정 및 삭제 시
	public void cancel(){
		this.isDeleted = true;
	}
}
