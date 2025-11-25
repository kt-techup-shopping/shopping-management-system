package com.shop.domain.product.request;

import java.util.Arrays;

import org.apache.logging.log4j.util.Strings;

import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;

public enum ProductSort {
	LATEST("latest"),
	PRICE_ASC("price_asc"),
	PRICE_DESC("price_desc"),
	DEFAULT("default");

	private final String sort;

	ProductSort(String sort) {
		this.sort = sort;
	}

	public boolean matches(String s) {
		return sort.equalsIgnoreCase(s);
	}

	public static ProductSort from(String sort) {
		if (Strings.isBlank(sort)) {
			return DEFAULT;
		}

		return Arrays.stream(values())
			.filter(v -> v.matches(sort))
			.findFirst()
			.orElseThrow(() -> new CustomException(ErrorCode.INVALID_SORT_OPTION));
	}
}

