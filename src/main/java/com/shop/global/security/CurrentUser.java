package com.shop.global.security;

import com.shop.domain.user.model.Role;

public interface CurrentUser {
	Long getId();

	String getLoginId();

	Role getRole();
}
