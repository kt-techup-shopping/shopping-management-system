package com.kt.domain.order;

import java.util.Arrays;

import org.apache.logging.log4j.util.Strings;

import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
	PENDING("결제대기"),
	COMPLETED("결제완료"),
	CANCELLED("주문취소"),
	SHIPPED("배송중"),
	DELIVERED("배송완료"),
	CONFIRMED("구매확정");

	private final String description;

	public boolean matches(String value) {
		return this.name().equalsIgnoreCase(value);
	}

	public static OrderStatus from(String status) {
		if (Strings.isBlank(status)) {
			return null;
		}
		return Arrays.stream(values())
			.filter(v -> v.matches(status))
			.findFirst()
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_ORDER_STATUS));
	}
}
