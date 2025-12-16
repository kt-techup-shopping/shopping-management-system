package com.shop.user.response;

import com.querydsl.core.annotations.QueryProjection;
import com.shop.domain.user.Gender;
import com.shop.domain.user.Status;

public record UserSearchResponse (
	Long id,
	String loginId,
	String name,
	String email,
	String mobile,
	Gender gender,
	Status status
) {
	@QueryProjection
	public UserSearchResponse {

	}
}
