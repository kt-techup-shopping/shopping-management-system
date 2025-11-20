package com.shop.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.user.model.User;
import com.shop.domain.user.request.UserUpdateRequest;
import com.shop.domain.user.service.UserService;
import com.shop.global.common.ApiResult;
import com.shop.domain.user.request.UserRequest;
import com.shop.domain.user.request.UserUpdatePasswordRequest;
import com.shop.global.security.CurrentUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
	private final UserService userService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResult<Void> create(@Valid @RequestBody UserRequest.Create request) {
		userService.create(request);
		return ApiResult.ok();
	}

	@GetMapping("/duplicate-login-id")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Boolean> isDuplicateLoginId(@RequestParam String loginId) {
		var result = userService.isDuplicateLoginId(loginId);

		return ApiResult.ok(result);
	}

	@PutMapping("/{id}/update-password")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> updatePassword(
		@PathVariable Long id,
		@RequestBody @Valid UserUpdatePasswordRequest request
	) {
		userService.changePassword(id, request.oldPassword(), request.newPassword());
		return ApiResult.ok();
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> delete(@PathVariable Long id) {
		userService.delete(id);
		return ApiResult.ok();
	}

	@GetMapping("/my-info")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<User> getMyInfo(
		@AuthenticationPrincipal CurrentUser currentUser
	) {
		// TODO: 어떤 정보를 받아올 지 논의
		var user = userService.detail(currentUser.getId());

		return ApiResult.ok(user);
	}

	@PutMapping("/my-info")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> updateMyInfo(
		@AuthenticationPrincipal CurrentUser currentUser,
		@RequestBody @Valid UserUpdateRequest request
	) {
		userService.update(currentUser.getId(), request.name(), request.email(), request.mobile());

		return ApiResult.ok();
	}

	@PostMapping("/withdrawal")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> withdrawal(
		@AuthenticationPrincipal CurrentUser currentUser
	) {
		userService.delete(currentUser.getId());

		return ApiResult.ok();
	}
}