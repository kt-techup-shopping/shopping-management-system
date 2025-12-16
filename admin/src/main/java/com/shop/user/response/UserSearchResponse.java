package com.shop.user.response;

import com.shop.domain.user.Gender;
import com.shop.domain.user.Status;
import com.shop.repository.user.response.UserSearchQueryResponse;

public record UserSearchResponse (
	Long id,
	String loginId,
	String name,
	String email,
	String mobile,
	Gender gender,
	Status status
) {
	public static UserSearchResponse from(UserSearchQueryResponse q) {
		return new UserSearchResponse(
			q.id(),
			q.loginId(),
			q.name(),
			q.email(),
			q.mobile(),
			q.gender(),
			q.status()
		);
	}
}
