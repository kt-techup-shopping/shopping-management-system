package com.shop.domain.auth.response;

public record LoginResponse(
	String accessToken,
	String refreshToken
) {
}
