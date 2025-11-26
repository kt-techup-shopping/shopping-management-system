package com.shop.domain.user.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.user.model.Gender;
import com.shop.domain.user.response.UserDetailResponse;
import com.shop.domain.user.response.UserSearchResponse;
import com.shop.domain.user.service.UserService;
import com.shop.global.common.ApiResult;
import com.shop.global.common.Paging;
import com.shop.domain.user.request.UserUpdateRequest;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
	private final UserService userService;

	// 유저 리스트 조회
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Page<UserSearchResponse>> getUserList(
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) Gender gender,
		@RequestParam(required = false) Boolean activeOnly,
		@Parameter Paging paging
	) {
		var userList = userService.searchUsers(keyword, gender, activeOnly, paging.sort(), paging.toPageable());

		return ApiResult.ok(userList);
	}

	// 유저 상세 조회
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<UserDetailResponse> detail(@PathVariable Long id) {
		var user = userService.detail(id);

		return ApiResult.ok(new UserDetailResponse(
			user.getId(),
			user.getName(),
			user.getEmail()
		));
	}

	// 유저 정보 수정
	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> update(@PathVariable Long id, @RequestBody @Valid UserUpdateRequest request) {
		userService.update(id, request.name(), request.email(), request.mobile());

		return ApiResult.ok();
	}

	// 유저 비활성화
	@PostMapping("/{id}/inactivate")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> updateUserStatusInactive(@PathVariable Long id) {
		userService.deactivateUser(id);

		return ApiResult.ok();
	}
}
