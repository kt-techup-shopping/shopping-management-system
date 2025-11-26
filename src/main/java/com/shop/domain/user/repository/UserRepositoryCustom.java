package com.shop.domain.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.shop.domain.user.model.Gender;
import com.shop.domain.user.model.Role;
import com.shop.domain.user.response.UserSearchResponse;

public interface UserRepositoryCustom {
	Page<UserSearchResponse> search(String keyword, Gender gender, Boolean activeOnly, Role user, String sort,
		PageRequest pageable);
}
