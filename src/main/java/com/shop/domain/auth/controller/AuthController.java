package com.shop.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.auth.service.AuthService;
import com.shop.domain.user.request.UserCreateRequest;
import com.shop.domain.user.service.UserService;
import com.shop.global.common.ApiResult;
import com.shop.domain.auth.request.LoginRequest;
import com.shop.domain.auth.response.LoginResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;
	private final UserService userService;

	@PostMapping("/login")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
		var pair = authService.login(request.loginId(), request.password());

		return ApiResult.ok(new LoginResponse(pair.getFirst(), pair.getSecond()));
	}

	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResult<Void> signup(@RequestBody @Valid UserCreateRequest request) {
		userService.create(request);

		return ApiResult.ok();
	}
}
