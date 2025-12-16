package com.shop.repository.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.shop.domain.user.Gender;
import com.shop.domain.user.Role;
import com.shop.repository.user.response.UserSearchQueryResponse;

public interface UserRepositoryCustom {
	Page<UserSearchQueryResponse> search(String keyword, Gender gender, Boolean activeOnly, Role user, String sort,
		PageRequest pageable);
}
