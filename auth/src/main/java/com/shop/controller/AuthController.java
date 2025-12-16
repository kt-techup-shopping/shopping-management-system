package com.shop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shop.ApiResult;
import com.shop.ErrorCode;
import com.shop.docs.ApiErrorCodeExamples;
import com.shop.request.LoginRequest;
import com.shop.request.RefreshTokenRequest;
import com.shop.response.LoginResponse;
import com.shop.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "인증", description = "인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
	private final AuthService authService;

	@Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인합니다.")
	@ApiErrorCodeExamples({
		ErrorCode.FAIL_LOGIN,
		ErrorCode.ACCOUNT_INACTIVATED,
	})
	@PostMapping("/login")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
		var pair = authService.login(request.loginId(), request.password());
		return ApiResult.ok(new LoginResponse(pair.getFirst(), pair.getSecond()));
	}

	@Operation(summary = "토큰 재발급", description = "Refresh Token을 이용해 Access Token을 재발급합니다.")
	@ApiErrorCodeExamples({
		ErrorCode.INVALID_REFRESH_TOKEN,
		ErrorCode.NOT_FOUND_USER
	})
	@PostMapping("/refresh")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<LoginResponse> postRefreshToken(@RequestBody @Valid RefreshTokenRequest request) {
		var pair = authService.refresh(request.refreshToken());
		return ApiResult.ok(new LoginResponse(pair.getFirst(), pair.getSecond()));
	}
}
