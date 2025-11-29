package com.shop.domain.delivery.response;

import com.shop.domain.delivery.model.Delivery;
import com.shop.domain.delivery.model.DeliveryStatus;

public record DeliveryResponse(
	Long orderId,
	DeliveryStatus deliveryStatus,
	String deliveryStatusDescription,
	String waybillNo
) {

	public static DeliveryResponse from(Delivery delivery) {
		return new DeliveryResponse(
			delivery.getOrder().getId(),
			delivery.getDeliveryStatus(),
			delivery.getDeliveryStatus().getDescription(),
			delivery.getWaybillNo()
		);
	}

	public boolean isPending() {
		return deliveryStatus == DeliveryStatus.PENDING;
	}

	public boolean isReady() {
		return deliveryStatus == DeliveryStatus.READY;
	}

	public boolean isShipping() {
		return deliveryStatus == DeliveryStatus.SHIPPING;
	}

	public boolean isDelivered() {
		return deliveryStatus == DeliveryStatus.DELIVERED;
	}

	public boolean hasWaybillNo() {
		return waybillNo != null && !waybillNo.trim().isEmpty();
	}
}