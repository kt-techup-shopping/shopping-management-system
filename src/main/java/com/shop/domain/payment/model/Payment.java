package com.shop.domain.payment.model;

import com.shop.global.common.BaseEntity;
import com.shop.domain.order.model.Order;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import lombok.Getter;

@Entity
@Getter
public class Payment extends BaseEntity {
	private Long totalPrice;
	private Long deliveryFee;
	@Enumerated(EnumType.STRING)
	private PaymentType type;

	@OneToOne
	private Order order;
}
