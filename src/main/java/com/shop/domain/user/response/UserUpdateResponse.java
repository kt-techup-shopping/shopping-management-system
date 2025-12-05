package com.shop.domain.user.response;

import com.shop.domain.user.model.User;

public record UserUpdateResponse (
	Long id,
	String name,
	String email,
	String mobile
){
	public static UserUpdateResponse of(User user) {
		return new UserUpdateResponse(
			user.getId(),
			user.getName(),
			user.getEmail(),
			user.getMobile()
		);
	}
}
