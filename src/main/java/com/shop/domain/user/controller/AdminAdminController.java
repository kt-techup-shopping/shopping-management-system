package com.shop.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.user.request.UserCreateRequest;
import com.shop.domain.user.request.UserUpdateRequest;
import com.shop.domain.user.service.AdminService;
import com.shop.domain.user.service.UserService;
import com.shop.global.common.ApiResult;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/admins")
public class AdminAdminController {
	private final AdminService adminService;
	private final UserService userService;

	// 관리자가 관리자 생성
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResult<Void> create(@RequestBody @Valid UserCreateRequest request) {
		userService.createAdmin(request);

		return ApiResult.ok();
	}

	// 관리자 정보 수정
	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> updateAdminInfo(
		@RequestBody @Valid UserUpdateRequest request,
		@PathVariable Long id) {
		userService.update(id, request.name(), request.email(), request.mobile());

		return ApiResult.ok();
	}

	// 관리자 권한 삭제
	@PutMapping("{id}/delete")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> updateUserRoleToUser(@PathVariable Long id) {
		adminService.updateUserRoleToUser(id);

		return ApiResult.ok();
	}
}
