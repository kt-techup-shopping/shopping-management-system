package com.shop.domain.user.response;

import com.shop.domain.user.model.Status;
import com.shop.domain.user.model.User;

public record UserStatusResponse(
	Long id,
	Status status
) {
	public static UserStatusResponse of(User user) {
		return new UserStatusResponse(
			user.getId(),
			user.getStatus()
		);
	}
}
