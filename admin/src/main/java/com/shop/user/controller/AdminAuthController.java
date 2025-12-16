package com.shop.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shop.ApiResult;
import com.shop.ErrorCode;
import com.shop.docs.ApiErrorCodeExample;
import com.shop.docs.ApiErrorCodeExamples;
import com.shop.security.CurrentUser;
import com.shop.service.AuthService;
import com.shop.user.request.UserCreateRequest;
import com.shop.user.request.UserUpdatePasswordRequest;
import com.shop.user.response.UserCreateResponse;
import com.shop.user.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "사용자(관리자)", description = "관리자용 인증 API")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/auth")
public class AdminAuthController {
	private final AdminService adminService;
	private final AuthService authService;

	@Operation(summary = "회원가입", description = "새로운 관리자를 등록합니다.")
	@ApiErrorCodeExample(ErrorCode.EXIST_USER)
	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResult<UserCreateResponse> signup(@RequestBody @Valid UserCreateRequest request) {
		var user = adminService.createAdmin(request);
		return ApiResult.ok(UserCreateResponse.from(user));
	}

	@Operation(summary = "로그아웃", description = "현재 로그인된 관리자를 로그아웃합니다.")
	@PostMapping("/logout")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> logout(@AuthenticationPrincipal CurrentUser currentUser) {
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
		adminService.changePassword(currentUser.getId(), request.oldPassword(), request.newPassword());
		return ApiResult.ok();
	}
}
