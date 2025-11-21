package com.shop.domain.user.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;
import com.shop.domain.user.model.User;


public interface UserRepository extends JpaRepository<User, Long> {
	Boolean existsByLoginId(String loginId);

	Optional<User> findByLoginId(String loginId);

	@Query("""
	SELECT exists (SELECT u FROM User u WHERE u.loginId = ?1)
""")
	Boolean existsByLoginIdJPQL(String loginId);

	Page<User> findAllByNameContaining(String name, Pageable pageable);

	default User findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}
}
