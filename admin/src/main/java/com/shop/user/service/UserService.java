package com.shop.user.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.ErrorCode;
import com.shop.Preconditions;
import com.shop.domain.user.Gender;
import com.shop.domain.user.Role;
import com.shop.domain.user.User;
import com.shop.repository.user.UserRepository;
import com.shop.user.request.UserCreateRequest;
import com.shop.user.response.UserSearchResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public User create(UserCreateRequest request) {
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

		return newUser;
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

	public void isDuplicateLoginId(String loginId) {
		var result = userRepository.existsByLoginId(loginId);
		Preconditions.validate(!result, ErrorCode.EXIST_LOGINID);
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
		var search =  userRepository.search(keyword, gender, activeOnly, Role.USER, sort, pageable);

		return search.map(UserSearchResponse::from);
	}

	public User detail(Long id) {
		return userRepository.findByIdOrThrow(id, ErrorCode.NOT_FOUND_USER);
	}

	public User update(Long id, String name, String email, String mobile) {
		var user = userRepository.findByIdOrThrow(id, ErrorCode.NOT_FOUND_USER);

		user.update(name, email, mobile);

		return user;
	}

	public void delete(Long id) {
		var user = userRepository.findByIdOrThrow(id, ErrorCode.NOT_FOUND_USER);

		user.delete();
	}

	public User deactivateUser(Long id) {
		var user = userRepository.findByIdOrThrow(id, ErrorCode.NOT_FOUND_USER);

		user.deactivate();

		return user;
	}
}
