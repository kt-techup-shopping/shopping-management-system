package com.shop.delivery.response;

import com.shop.domain.delivery.Delivery;
import com.shop.domain.delivery.DeliveryStatus;

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
}