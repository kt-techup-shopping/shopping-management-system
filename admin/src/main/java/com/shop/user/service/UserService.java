package com.shop.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.ErrorCode;
import com.shop.domain.user.Gender;
import com.shop.domain.user.Role;
import com.shop.domain.user.User;
import com.shop.repository.user.UserRepository;
import com.shop.user.response.UserSearchResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
	private final UserRepository userRepository;

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

	public User deactivateUser(Long id) {
		var user = userRepository.findByIdOrThrow(id, ErrorCode.NOT_FOUND_USER);

		user.deactivate();

		return user;
	}
}
