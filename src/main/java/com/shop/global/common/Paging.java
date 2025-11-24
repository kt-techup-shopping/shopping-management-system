package com.shop.global.common;

import org.springframework.data.domain.PageRequest;

public record Paging(
	int page,
	int size,
	//todo: 정렬기능도 추가 예정
	String sort,
	String order
) {
	public PageRequest toPageable() {
		return PageRequest.of(page - 1, size);
	}
}
