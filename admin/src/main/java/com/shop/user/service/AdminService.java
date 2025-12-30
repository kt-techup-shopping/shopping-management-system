package com.shop.user.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.ErrorCode;
import com.shop.Preconditions;
import com.shop.domain.user.Role;
import com.shop.domain.user.User;
import com.shop.encoder.PasswordEncoder;
import com.shop.repository.user.UserRepository;
import com.shop.user.request.UserCreateRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public User updateUserRoleToUser(Long id) {
		var user = userRepository.findByIdOrThrow(id, ErrorCode.NOT_FOUND_USER);

		Preconditions.validate(user.getRole() == Role.ADMIN, ErrorCode.NOT_USER_ROLE_ADMIN);

		user.demoteToUser();

		return user;
	}

	public User createAdmin(UserCreateRequest request) {
		Preconditions.validate(!userRepository.existsByLoginId(request.loginId()), ErrorCode.EXIST_USER);

		var newAdmin = User.admin(
			request.loginId(),
			UUID.randomUUID(),
			passwordEncoder.encode(request.password()),
			request.name(),
			request.email(),
			request.mobile(),
			request.gender(),
			request.birthday()
		);

		userRepository.save(newAdmin);

		return newAdmin;
	}

	public void changePassword(Long id, String oldPassword, String newPassword) {
		var user = userRepository.findByIdOrThrow(id, ErrorCode.NOT_FOUND_USER);

		Preconditions.validate(passwordEncoder.matches(oldPassword, user.getPassword()),
			ErrorCode.DOES_NOT_MATCH_OLD_PASSWORD);
		Preconditions.validate(!oldPassword.equals(newPassword), ErrorCode.CAN_NOT_ALLOWED_SAME_PASSWORD);

		user.changePassword(passwordEncoder.encode(newPassword));
	}

	public User update(Long id, String name, String email, String mobile) {
		var user = userRepository.findByIdOrThrow(id, ErrorCode.NOT_FOUND_USER);

		user.update(name, email, mobile);

		return user;
	}
}
