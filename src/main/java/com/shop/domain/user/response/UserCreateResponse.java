package com.shop.domain.user.response;

import com.shop.domain.user.model.Role;
import com.shop.domain.user.model.User;

public record UserCreateResponse (
	Long id,
	String name,
	Role role
){
	public static UserCreateResponse of(User user) {
		return new UserCreateResponse(
			user.getId(),
			user.getName(),
			user.getRole()
		);
	}
}
