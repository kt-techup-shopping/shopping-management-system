package com.shop.user.response;

import com.shop.domain.user.Status;
import com.shop.domain.user.User;

public record UserStatusResponse(
	Long id,
	Status status
) {
	public static UserStatusResponse from(User user) {
		return new UserStatusResponse(
			user.getId(),
			user.getStatus()
		);
	}
}
