package com.shop.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shop.domain.user.service.AdminService;
import com.shop.global.common.ApiResult;
import com.shop.global.security.CurrentUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/admins")
public class AdminAdminController {
	private final AdminService adminService;

	@PutMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> updateUserRoleToAdmin(@AuthenticationPrincipal CurrentUser currentUser) {
		adminService.updateUserRoleToAdmin(currentUser.getId());

		return ApiResult.ok();
	}

	@PutMapping("{id}/delete")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> updateUserRoleToUser(@PathVariable Long id) {
		adminService.updateUserRoleToUser(id);

		return ApiResult.ok();
	}
}
