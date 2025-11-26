package com.shop.domain.user.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.user.model.Gender;
import com.shop.domain.user.model.Role;
import com.shop.domain.user.model.User;
import com.shop.domain.user.request.UserCreateRequest;
import com.shop.domain.user.response.UserSearchResponse;
import com.shop.global.common.ErrorCode;
import com.shop.global.common.Preconditions;
import com.shop.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public void create(UserCreateRequest request) {
		Preconditions.validate(!userRepository.existsByLoginId(request.loginId()), ErrorCode.EXIST_USER);

		var newUser = User.normalUser(
			request.loginId(),
			UUID.randomUUID(),
			passwordEncoder.encode(request.password()),
			request.name(),
			request.email(),
			request.mobile(),
			request.gender(),
			request.birthday()
		);

			userRepository.save(newUser);
	}

	public void createAdmin(UserCreateRequest request) {
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
	}

	public boolean isDuplicateLoginId(String loginId) {
		return userRepository.existsByLoginId(loginId);
	}

	public void changePassword(Long id, String oldPassword, String newPassword) {
		var user = userRepository.findByIdOrThrow(id, ErrorCode.NOT_FOUND_USER);

		Preconditions.validate(passwordEncoder.matches(oldPassword, user.getPassword()),
			ErrorCode.DOES_NOT_MATCH_OLD_PASSWORD);
		Preconditions.validate(!oldPassword.equals(newPassword), ErrorCode.CAN_NOT_ALLOWED_SAME_PASSWORD);

		user.changePassword(passwordEncoder.encode(newPassword));
	}

	public Page<UserSearchResponse> searchUsers(String keyword, Gender gender, Boolean activeOnly, String sort,
		PageRequest pageable) {
		return userRepository.search(keyword, gender, activeOnly, Role.USER, sort, pageable);
	}

	public User detail(Long id) {
		return userRepository.findByIdOrThrow(id, ErrorCode.NOT_FOUND_USER);
	}

	public void update(Long id, String name, String email, String mobile) {
		var user = userRepository.findByIdOrThrow(id, ErrorCode.NOT_FOUND_USER);

		user.update(name, email, mobile);
	}

	public void delete(Long id) {
		var user = userRepository.findByIdOrThrow(id, ErrorCode.NOT_FOUND_USER);

		user.delete();
	}

	public void deactivateUser(Long id) {
		var user = userRepository.findByIdOrThrow(id, ErrorCode.NOT_FOUND_USER);

		user.deactivate();
	}
}
