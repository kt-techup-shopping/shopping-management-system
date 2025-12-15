package com.kt.domain.payment;

import com.shop.domain.order.model.Order;
import com.shop.global.common.BaseEntity;
import com.shop.global.common.ErrorCode;
import com.shop.global.common.Preconditions;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Payment extends BaseEntity {
	private Long totalAmount;
	private Long discountAmount;
	private Long deliveryFee;
	private Long finalAmount;

	@Enumerated(EnumType.STRING)
	private PaymentStatus status;

	@Enumerated(EnumType.STRING)
	private PaymentType type;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	// @OneToOne(fetch = FetchType.LAZY)
	private Order order;

	private Payment(Long totalAmount, Long discountAmount, Long deliveryFee, Long finalAmount, PaymentType type,
		Order order) {
		this.totalAmount = totalAmount;
		this.discountAmount = discountAmount;
		this.deliveryFee = deliveryFee;
		this.finalAmount = finalAmount;
		this.status = PaymentStatus.PENDING;
		this.type = type;
		this.order = order;
	}

	public static Payment create(Long totalAmount, Long discountAmount, Long deliveryFee, PaymentType type, Order order) {
		Preconditions.validate(order != null, ErrorCode.REQUIRED_ORDER_FOR_PAYMENT);

		Long finalAmount = totalAmount - discountAmount + deliveryFee;

		return new Payment(
			totalAmount,
			discountAmount,
			deliveryFee,
			finalAmount,
			type,
			order
		);
	}

	public boolean isPending() {
		return this.status == PaymentStatus.PENDING;
	}

	public void complete() {
		this.status = PaymentStatus.COMPLETED;
	}

	public void cancel() {
		this.status = PaymentStatus.CANCELED;
	}
}
