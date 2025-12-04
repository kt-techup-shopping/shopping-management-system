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
import com.shop.global.common.ErrorCode;
import com.shop.global.common.Paging;
import com.shop.domain.user.request.UserUpdateRequest;
import com.shop.global.docs.ApiErrorCodeExample;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "사용자(관리자)", description = "관리자용 사용자 관리 API")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/users")
public class AdminUserController {
	private final UserService userService;

	// 유저 리스트 조회
	@Operation(summary = "유저 리스트 조회", description = "사용자 목록을 조회하고, 키워드, 성별, 활성 상태, 정렬 기준, 페이징을 적용할 수 있습니다.")
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Page<UserSearchResponse>> getUserList(
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) Gender gender,
		@RequestParam(required = false) Boolean activeOnly,
		@RequestParam(required = false) String sort,
		@Parameter Paging paging
	) {
		var userList = userService.searchUsers(keyword, gender, activeOnly, sort, paging.toPageable());
		return ApiResult.ok(userList);
	}

	// 유저 상세 조회
	@Operation(summary = "유저 상세 조회", description = "특정 사용자의 상세 정보를 조회합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_USER)
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
	@Operation(summary = "유저 정보 수정", description = "특정 사용자의 이름, 이메일, 휴대폰 정보를 수정합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_USER)
	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> update(@PathVariable Long id, @RequestBody @Valid UserUpdateRequest request) {
		userService.update(id, request.name(), request.email(), request.mobile());
		return ApiResult.ok();
	}

	// 유저 비활성화
	@Operation(summary = "유저 비활성화", description = "특정 사용자를 비활성화 처리합니다.")
	@ApiErrorCodeExample(ErrorCode.NOT_FOUND_USER)
	@PostMapping("/{id}/inactivate")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> updateUserStatusInactive(@PathVariable Long id) {
		userService.deactivateUser(id);
		return ApiResult.ok();
	}
}

