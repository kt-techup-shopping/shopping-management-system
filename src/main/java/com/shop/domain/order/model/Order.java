package com.shop.domain.order.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.shop.domain.delivery.model.Delivery;
import com.shop.domain.orderproduct.model.OrderProduct;
import com.shop.domain.user.model.User;
import com.shop.global.common.BaseEntity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor
public class Order extends BaseEntity {
	@Embedded
	private Receiver receiver;
	@Enumerated(EnumType.STRING)
	private OrderStatus status;
	private LocalDateTime deliveredAt;

	@OneToOne(mappedBy = "order")
	private Delivery delivery;

	// Order 생성 시 Delivery 자동 생성
	@PostPersist
	private void createDelivery() {
		if (this.delivery == null) {
			this.delivery = new Delivery(this);
		}
	}

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@OneToMany(mappedBy = "order")
	private List<OrderProduct> orderProducts = new ArrayList<>();

	private Order(Receiver receiver, User user) {
		this.receiver = receiver;
		this.user = user;
		this.deliveredAt = LocalDateTime.now().plusDays(3);
		this.status = OrderStatus.PENDING;
	}

	public static Order create(Receiver receiver, User user) {
		return new Order(
			receiver,
			user
		);
	}

	public void mapToOrderProduct(OrderProduct orderProduct) {
		this.orderProducts.add(orderProduct);
	}

	//하나의 오더는 여러개의 상품을 가질수있음
	// 1:N
	//하나의 상품은 여러개의 오더를 가질수있음
	// 1:N

	// 주문생성
	// 주문상태변경
	// 주문생성완료재고차감
	// 배송받는사람정보변경
	// 주문취소
}
