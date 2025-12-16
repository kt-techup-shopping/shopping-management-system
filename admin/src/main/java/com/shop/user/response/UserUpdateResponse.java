package com.shop.user.response;

import com.shop.domain.user.User;

public record UserUpdateResponse (
	Long id,
	String name,
	String email,
	String mobile
){
	public static UserUpdateResponse from(User user) {
		return new UserUpdateResponse(
			user.getId(),
			user.getName(),
			user.getEmail(),
			user.getMobile()
		);
	}
}
