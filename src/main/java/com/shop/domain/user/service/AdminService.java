package com.shop.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.user.model.Role;
import com.shop.domain.user.model.User;
import com.shop.domain.user.repository.UserRepository;
import com.shop.global.common.ErrorCode;
import com.shop.global.common.Preconditions;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {
	private final UserRepository userRepository;

	public User updateUserRoleToUser(Long id) {
		var user = userRepository.findByIdOrThrow(id, ErrorCode.NOT_FOUND_USER);

		Preconditions.validate(user.getRole() == Role.ADMIN, ErrorCode.NOT_USER_ROLE_ADMIN);

		user.demoteToUser();

		return user;
	}
}
