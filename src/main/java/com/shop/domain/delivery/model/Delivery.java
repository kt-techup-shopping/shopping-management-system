package com.shop.domain.delivery.model;

import com.shop.domain.order.model.Order;
import com.shop.global.common.BaseEntity;
import com.shop.global.common.ErrorCode;
import com.shop.global.common.Preconditions;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Delivery extends BaseEntity {
	@OneToOne
	@JoinColumn(name = "order_id")
	private Order order;

	@Enumerated(EnumType.STRING)
	private DeliveryStatus deliveryStatus;

	private String waybillNo;

	// 자동적으로 Pending상태로 생성
	public Delivery(Order order) {
		this.order = order;
		this.deliveryStatus = DeliveryStatus.PENDING;
	}

	// Pending 상태로 변경 , 송장번호 삭제
	public void updatePending() {
		deliveryStatus = DeliveryStatus.PENDING;
		this.waybillNo = null;
	}

	// Ready 상태로 변경, 송장번호 입력 필수
	public void updateReady(String waybillNo) {
		Preconditions.validate(waybillNo != null && !waybillNo.trim().isEmpty(), ErrorCode.WAYBILL_NO_REQUIRED);
		deliveryStatus = DeliveryStatus.READY;
		this.waybillNo = waybillNo;
	}

	// Shipping 상태로 변경, 송장번호 유지
	public void updateShipping() {
		deliveryStatus = DeliveryStatus.SHIPPING;
	}

	// Delivered 상태로 변경, 송장번호 유지
	public void updateDelivered() {
		deliveryStatus = DeliveryStatus.DELIVERED;
	}
}
