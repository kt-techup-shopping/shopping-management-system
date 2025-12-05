package com.shop.domain.user.response;

import com.shop.domain.user.model.User;

public record UserDetailResponse(
	Long id,
	String name,
	String email
) {
	public static UserDetailResponse from(User user) {
		return new UserDetailResponse(
			user.getId(),
			user.getName(),
			user.getEmail()
		);
	}
}
