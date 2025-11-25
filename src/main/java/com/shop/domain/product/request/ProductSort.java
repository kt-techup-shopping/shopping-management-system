package com.shop.domain.product.request;

import java.util.Arrays;

import org.apache.logging.log4j.util.Strings;

import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;

public enum ProductSort {
	LATEST("latest"),			// 최신순
	PRICE_ASC("price_asc"),		// 가격 낮은순
	PRICE_DESC("price_desc"),	// 가격 높은순
	DEFAULT("default");			// 기본값 (ID)

	private final String sort;

	ProductSort(String sort) {
		this.sort = sort;
	}

	// 요청값과 enum 값이 일치하는지 확인
	public boolean matches(String s) {
		return sort.equalsIgnoreCase(s);
	}

	public static ProductSort from(String sort) {
		// 정렬 옵션이 없으면 기본값 반환
		if (Strings.isBlank(sort)) {
			return DEFAULT;
		}

		// 요청값과 일치하는 enum 반환
		return Arrays.stream(values())
			.filter(v -> v.matches(sort))
			.findFirst()
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_SORT_OPTION)); // 유효하지 않은 정렬 요청 시 예외 처리
	}
}

