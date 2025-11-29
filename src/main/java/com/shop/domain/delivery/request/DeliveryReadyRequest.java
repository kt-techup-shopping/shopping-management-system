package com.shop.domain.delivery.request;

import jakarta.validation.constraints.NotNull;

public record DeliveryReadyRequest(
	@NotNull
	String waybillNo
) {

	// Compact Constructor - 송장번호 형식 검증
	public DeliveryReadyRequest {
		if (waybillNo != null) {
			waybillNo = waybillNo.trim();
		}
	}

	// 정적 팩토리 메서드
	public static DeliveryReadyRequest of(String waybillNo) {
		return new DeliveryReadyRequest(waybillNo);
	}
}