package com.shop.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.shop.domain.auth.request.RefreshTokenRequest;
import com.shop.domain.auth.service.AuthService;
import com.shop.domain.user.request.UserCreateRequest;
import com.shop.domain.user.request.UserUpdatePasswordRequest;
import com.shop.domain.user.response.UserCreateResponse;
import com.shop.domain.user.service.UserService;
import com.shop.global.common.ApiResult;
import com.shop.domain.auth.request.LoginRequest;
import com.shop.domain.auth.response.LoginResponse;
import com.shop.global.common.ErrorCode;
import com.shop.global.docs.ApiErrorCodeExample;
import com.shop.global.docs.ApiErrorCodeExamples;
import com.shop.global.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "인증", description = "인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
	private final AuthService authService;
	private final UserService userService;

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

	@Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
	@ApiErrorCodeExample(ErrorCode.EXIST_USER)
	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResult<UserCreateResponse> signup(@RequestBody @Valid UserCreateRequest request) {
		var user = userService.create(request);
		return ApiResult.ok(UserCreateResponse.of(user));
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

	@Operation(summary = "로그아웃", description = "현재 로그인된 사용자를 로그아웃합니다.")
	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> postLogout(@AuthenticationPrincipal CurrentUser currentUser) {
		authService.logout(currentUser.getId());
		return ApiResult.ok();
	}

	@Operation(summary = "비밀번호 변경", description = "현재 비밀번호를 확인하고 새 비밀번호로 변경합니다.")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_FOUND_USER,
		ErrorCode.DOES_NOT_MATCH_OLD_PASSWORD,
		ErrorCode.CAN_NOT_ALLOWED_SAME_PASSWORD,
	})
	@PostMapping("/reset-password/confirm")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> postResetPasswordConfirm(
		@AuthenticationPrincipal CurrentUser currentUser,
		@RequestBody @Valid UserUpdatePasswordRequest request
	) {
		userService.changePassword(currentUser.getId(), request.oldPassword(), request.newPassword());
		return ApiResult.ok();
	}
}
