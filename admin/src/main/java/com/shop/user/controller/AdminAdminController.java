package com.shop.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shop.ApiResult;
import com.shop.ErrorCode;
import com.shop.docs.ApiErrorCodeExample;
import com.shop.docs.ApiErrorCodeExamples;
import com.shop.user.request.UserCreateRequest;
import com.shop.user.request.UserUpdateRequest;
import com.shop.user.response.UserCreateResponse;
import com.shop.user.response.UserStatusResponse;
import com.shop.user.response.UserUpdateResponse;
import com.shop.user.service.AdminService;
import com.shop.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "관리자(관리자용)", description = "관리자 계정 관리 API")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/admins")
public class AdminAdminController {
	private final AdminService adminService;
	private final UserService userService;

	// 관리자가 관리자 생성
	@Operation(summary = "관리자 생성", description = "관리자가 새로운 관리자 계정을 생성합니다.")
	@ApiErrorCodeExample(ErrorCode.EXIST_USER)
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResult<UserCreateResponse> create(@RequestBody @Valid UserCreateRequest request) {
		var admin = userService.createAdmin(request);
		return ApiResult.ok(UserCreateResponse.from(admin));
	}

	// 관리자 정보 수정
	@Operation(summary = "관리자 정보 수정", description = "특정 관리자 계정의 이름, 이메일, 휴대폰 정보를 수정합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_USER)
	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<UserUpdateResponse> updateAdminInfo(
		@RequestBody @Valid UserUpdateRequest request,
		@PathVariable Long id
	) {
		var user = userService.update(id, request.name(), request.email(), request.mobile());
		return ApiResult.ok(UserUpdateResponse.from(user));
	}

	// 관리자 권한 삭제
	@PutMapping("{id}/delete")
	@ApiErrorCodeExamples({
		ErrorCode.NOT_FOUND_USER,
		ErrorCode.NOT_USER_ROLE_ADMIN
	})
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "관리자 권한 삭제", description = "관리자 계정의 권한을 일반 사용자로 변경합니다.")
	public ApiResult<UserStatusResponse> updateUserRoleToUser(@PathVariable Long id) {
		var user = adminService.updateUserRoleToUser(id);
		return ApiResult.ok(UserStatusResponse.from(user));
	}
}
