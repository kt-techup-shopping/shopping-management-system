package com.shop.global.common;

import org.springframework.data.domain.PageRequest;

public record Paging(
	int page,
	int size,
	//todo: 정렬기능도 추가 예정
	String sort,
	String order
) {
	public Paging {
		if (page == null || page < 1) {
			page = 1;  // 기본 페이지는 1
		}
		if (size == null || size < 1) {
			size = 10; // 기본 사이즈는 10
		}
		if (size > 100) {
			size = 100; // 최대 사이즈 제한
		}
	}

	public PageRequest toPageable() {
		return PageRequest.of(page - 1, size);
	}
}
