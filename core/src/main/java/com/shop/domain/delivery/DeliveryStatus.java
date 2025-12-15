package com.shop.domain.delivery;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryStatus {
	PENDING("주문 확인 중"),
	READY("배송 준비 중"),
	SHIPPING("배송 중"),
	DELIVERED("배송 완료");
	private final String description;
}
