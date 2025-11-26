package com.shop.global.common;

import org.springframework.data.domain.PageRequest;

public record Paging(
	Integer page,
	Integer size
) {
	private static final int DEFAULT_PAGE = 1;
	private static final int DEFAULT_SIZE = 10;

	// TODO: 정렬 기능 보완 예정
	public Paging {
		page = page == null ? DEFAULT_PAGE : page;
		size = size == null ? DEFAULT_SIZE : size;

		Preconditions.validate(page >= 1, ErrorCode.PAGE_INVALID);
		Preconditions.validate(size >= 1 && size <= 100, ErrorCode.SIZE_INVALID);
	}

	public PageRequest toPageable() {
		return PageRequest.of(page - 1, size);
	}
}
