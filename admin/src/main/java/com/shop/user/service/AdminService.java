package com.shop.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.ErrorCode;
import com.shop.Preconditions;
import com.shop.domain.user.Role;
import com.shop.domain.user.User;
import com.shop.repository.user.UserRepository;

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
