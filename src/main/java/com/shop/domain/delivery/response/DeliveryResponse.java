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
}