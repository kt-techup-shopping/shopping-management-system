package com.shop.service.user;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.shop.domain.user.model.Gender;
import com.shop.domain.user.model.Role;
import com.shop.domain.user.model.User;
import com.shop.domain.user.repository.UserRepository;
import com.shop.domain.user.response.UserSearchResponse;
import com.shop.domain.user.service.UserService;
import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	private User createUser() {
		return User.normalUser(
			"testUser",
			UUID.randomUUID(),
			"Test1234@",
			"test",
			"test@test.com",
			"010-0000-0000",
			Gender.MALE,
			LocalDate.now()
		);
	}

	@Test
	void 로그인ID_중복_없으면_성공() {
		String loginId = "testUser";
		given(userRepository.existsByLoginId(loginId)).willReturn(false);

		assertThatCode(() -> userService.isDuplicateLoginId(loginId))
			.doesNotThrowAnyException();

		then(userRepository)
			.should()
			.existsByLoginId(loginId);
	}

	@Test
	void 로그인ID_중복_있으면_실패() {
		String loginId = "testUser";
		given(userRepository.existsByLoginId(loginId)).willReturn(true);

		assertThatThrownBy(() -> userService.isDuplicateLoginId(loginId))
			.isInstanceOf(CustomException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.EXIST_LOGINID);

		then(userRepository)
			.should()
			.existsByLoginId(loginId);
	}

	@Test
	void 비밀번호_변경_성공() {
		Long userId = 1L;
		String oldPassword = "oldPassword";
		String newPassword = "newPassword";

		User user = createUser();

		given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER)).willReturn(user);
		given(passwordEncoder.matches(oldPassword, user.getPassword())).willReturn(true);
		given(passwordEncoder.encode(newPassword)).willReturn("encodedNewPW");

		userService.changePassword(userId, oldPassword, newPassword);

		assertThat(user.getPassword()).isEqualTo("encodedNewPW");
	}

	@Test
	void 기존_비밀번호와_다르면_실패() {
		Long userId = 1L;
		String oldPassword = "wrongPW";

		User user = createUser();

		given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER)).willReturn(user);
		given(passwordEncoder.matches(oldPassword, user.getPassword())).willReturn(false);

		assertThatThrownBy(() -> userService.changePassword(userId, oldPassword, "newPW"))
			.isInstanceOf(CustomException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.DOES_NOT_MATCH_OLD_PASSWORD);
	}

	@Test
	void 새로운_비밀번호와_동일하면_실패() {
		Long userId = 1L;
		String oldPassword = "samePW";
		String newPassword = "samePW";

		User user = createUser();

		given(userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER)).willReturn(user);
		given(passwordEncoder.matches(oldPassword, user.getPassword())).willReturn(true);

		assertThatThrownBy(() -> userService.changePassword(userId, oldPassword, newPassword))
			.isInstanceOf(CustomException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.CAN_NOT_ALLOWED_SAME_PASSWORD);
	}

	@Test
	void 유저_리스트_조회_성공() {
		String keyword = "test";
		Gender gender = Gender.MALE;
		Boolean activeOnly = true;
		String sort = "name";
		PageRequest pageable = PageRequest.of(0, 10);

		Page<UserSearchResponse> expectedPage = mock(Page.class);

		given(userRepository.search(keyword, gender, activeOnly, Role.USER, sort, pageable))
			.willReturn(expectedPage);

		var result = userService.searchUsers(keyword, gender, activeOnly, sort, pageable);

		then(userRepository).should().search(
			keyword,
			gender,
			activeOnly,
			Role.USER,
			sort,
			pageable
		);

		assertThat(result).isSameAs(expectedPage);
	}
}