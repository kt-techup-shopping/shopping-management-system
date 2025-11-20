package com.shop.domain.user.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.user.model.Status;
import com.shop.domain.user.model.User;
import com.shop.global.common.ErrorCode;
import com.shop.global.common.Preconditions;
import com.shop.domain.user.request.UserRequest;
import com.shop.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public void create(UserRequest.Create request) {
		Preconditions.validate(!userRepository.existsByLoginId(request.loginId()), ErrorCode.EXIST_USER);

		var newUser = User.normalUser(
			request.loginId(),
			UUID.randomUUID(),
			passwordEncoder.encode(request.password()),
			request.name(),
			request.email(),
			request.mobile(),
			request.gender(),
			request.birthday(),
			Status.ACTIVATED
		);

			userRepository.save(newUser);
	}

	public boolean isDuplicateLoginId(String loginId) {
		return userRepository.existsByLoginId(loginId);
	}

	public void changePassword(Long id, String oldPassword, String password) {
		var user = userRepository.findByIdOrThrow(id, ErrorCode.NOT_FOUND_USER);

		Preconditions.validate(user.getPassword().equals(oldPassword), ErrorCode.DOES_NOT_MATCH_OLD_PASSWORD);
		Preconditions.validate(!oldPassword.equals(password), ErrorCode.CAN_NOT_ALLOWED_SAME_PASSWORD);

		user.changePassword(password);
	}

	public Page<User> search(Pageable pageable, String keyword) {
		return userRepository.findAllByNameContaining(keyword, pageable);
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

		user.withdrawal();
	}
}
