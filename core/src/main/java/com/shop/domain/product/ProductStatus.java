package com.shop.domain.product;

import java.util.Arrays;

import com.shop.CustomException;
import com.shop.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductStatus {
	// 상품의 초기 상태는 판매중이다.
	ACTIVATED("판매중"),
	SOLD_OUT("품절"),
	IN_ACTIVATED("판매중지"),
	DELETED("삭제");

	private final String description;

	public boolean matches(String value) {
		return this.name().equalsIgnoreCase(value);
	}

	public static ProductStatus from(String value) {
		return Arrays.stream(values())
			.filter(v -> v.matches(value))
			.findFirst()
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_PRODUCT_STATUS));
	}

}
