package com.shop.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shop.ApiResult;
import com.shop.ErrorCode;
import com.shop.docs.ApiErrorCodeExample;
import com.shop.domain.user.User;
import com.shop.security.CurrentUser;
import com.shop.user.request.UserUpdateRequest;
import com.shop.user.response.UserUpdateResponse;
import com.shop.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "사용자", description = "사용자 계정 및 정보 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
	private final UserService userService;

	@Operation(summary = "로그인 ID 중복 체크", description = "입력한 로그인 ID가 이미 존재하는지 확인합니다.")
	@GetMapping("/duplicate-login-id")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> isDuplicateLoginId(@RequestParam String loginId) {
		userService.isDuplicateLoginId(loginId);
		return ApiResult.ok();
	}

	@Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_USER)
	@GetMapping("/my-info")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<User> getMyInfo(
		@AuthenticationPrincipal CurrentUser currentUser
	) {
		var user = userService.detail(currentUser.getId());
		return ApiResult.ok(user);
	}

	@Operation(summary = "내 정보 수정", description = "현재 로그인한 사용자의 이름, 이메일, 휴대폰 정보를 수정합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_USER)
	@PutMapping("/my-info")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<UserUpdateResponse> putMyInfo(
		@AuthenticationPrincipal CurrentUser currentUser,
		@RequestBody @Valid UserUpdateRequest request
	) {
		var user = userService.update(currentUser.getId(), request.name(), request.email(), request.mobile());
		return ApiResult.ok(UserUpdateResponse.from(user));
	}

	@Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자가 계정을 탈퇴합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_USER)
	@PutMapping("/withdrawal")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> deleteUser(
		@AuthenticationPrincipal CurrentUser currentUser
	) {
		userService.delete(currentUser.getId());
		return ApiResult.ok();
	}
}
