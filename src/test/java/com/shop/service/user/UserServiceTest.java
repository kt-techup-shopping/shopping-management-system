package com.shop.service.user;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shop.domain.user.repository.UserRepository;
import com.shop.domain.user.service.UserService;
import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	@BeforeEach
	void setup() {
		userRepository.deleteAll();
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
}