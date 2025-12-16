package com.shop.user.response;

import com.shop.domain.user.Role;
import com.shop.domain.user.User;

public record UserCreateResponse (
	Long id,
	String name,
	Role role
){
	public static UserCreateResponse from(User user) {
		return new UserCreateResponse(
			user.getId(),
			user.getName(),
			user.getRole()
		);
	}
}
