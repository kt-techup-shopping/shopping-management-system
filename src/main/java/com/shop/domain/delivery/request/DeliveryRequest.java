package com.shop.domain.delivery.request;

import com.shop.domain.delivery.model.DeliveryStatus;

import jakarta.validation.constraints.NotNull;

public record DeliveryRequest(
	@NotNull
	DeliveryStatus deliveryStatus,
	String waybillNo
) {
	public static DeliveryRequest pending() {
		return new DeliveryRequest(DeliveryStatus.PENDING, null);
	}

	public static DeliveryRequest ready(String waybillNo) {
		return new DeliveryRequest(DeliveryStatus.READY, waybillNo);
	}

	public static DeliveryRequest shipping() {
		return new DeliveryRequest(DeliveryStatus.SHIPPING, null);
	}

	public static DeliveryRequest delivered() {
		return new DeliveryRequest(DeliveryStatus.DELIVERED, null);
	}
}
