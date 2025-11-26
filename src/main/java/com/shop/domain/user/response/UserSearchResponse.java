package com.shop.domain.user.response;

import com.querydsl.core.annotations.QueryProjection;
import com.shop.domain.user.model.Gender;
import com.shop.domain.user.model.Status;

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
