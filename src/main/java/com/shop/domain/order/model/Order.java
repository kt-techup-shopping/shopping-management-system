package com.shop.domain.order.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.shop.domain.delivery.model.Delivery;
import com.shop.domain.payment.model.Payment;
import com.shop.global.common.BaseEntity;
import com.shop.domain.orderproduct.model.OrderProduct;
import com.shop.domain.user.model.User;
import com.shop.global.common.BaseEntity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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

	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
	private List<Payment> payments = new ArrayList<>();

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

	// 결제 대기 중인 결제가 있으면 요청 불가
	public boolean canRequestPayment() {
		return payments
			.stream()
			.noneMatch(Payment::isPending);
	}

	public Long calculateTotalAmount() {
		return orderProducts
			.stream()
			.mapToLong(op -> op
				.getProduct()
				.getPrice() * op.getQuantity())
			.sum();
	}

	public void addPayment(Payment payment) {
		this.payments.add(payment);
	}

	public boolean isPending() {
		return this.status == OrderStatus.PENDING;
	}

	public boolean isCompleted() {
		return this.status == OrderStatus.COMPLETED;
	}

	public void completePayment() {
		this.status = OrderStatus.COMPLETED;
	}

	public void resetToPending() {
		this.status = OrderStatus.PENDING;
	}
}
