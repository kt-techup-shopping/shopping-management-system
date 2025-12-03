package com.shop.service.user;



import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shop.domain.user.model.Gender;
import com.shop.domain.user.model.Role;
import com.shop.domain.user.model.User;
import com.shop.domain.user.repository.UserRepository;
import com.shop.domain.user.service.AdminService;
import com.shop.domain.user.service.UserService;
import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private AdminService adminService;

	@Test
	void 관리자_권한_변경_성공() {
		Long userId = 1L;
		var admin = User.admin(
			"admin",
			UUID.randomUUID(),
			"password",
			"admin"
			, "test@test.com",
			"010-0000-0000",
			Gender.MALE,
			LocalDate.now()
		);

		given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER)).willReturn(admin);

		adminService.updateUserRoleToUser(userId);

		assertThat(admin.getRole()).isEqualTo(Role.USER);
	}

	@Test
	void 관리자_아니면_권한_변경_실패() {
		Long userId = 1L;
		var user = User.normalUser(
			"test",
			UUID.randomUUID(),
			"password",
			"test"
			, "test@test.com",
			"010-0000-0000",
			Gender.MALE,
			LocalDate.now()
		);

		given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER)).willReturn(user);

		assertThatThrownBy(() -> adminService.updateUserRoleToUser(userId))
			.isInstanceOf(CustomException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.NOT_USER_ROLE_ADMIN);
	}
}