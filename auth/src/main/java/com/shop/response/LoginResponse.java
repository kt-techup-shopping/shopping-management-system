package com.shop.response;

public record LoginResponse(
	String accessToken,
	String refreshToken
) {
}
