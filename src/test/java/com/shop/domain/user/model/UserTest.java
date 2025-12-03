package com.shop.domain.user.model;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserTest {

	@Test
	void 일반_유저_생성() {

		var user = User.normalUser(
			"testUser",
			UUID.randomUUID(),
			"password",
			"test",
			"test@test.com",
			"010-0000-0000",
			Gender.MALE,
			LocalDate.now()
		);

		assertThat(user.getRole()).isEqualTo(Role.USER);
		assertThat(user.getStatus()).isEqualTo(Status.ACTIVE);
		assertThat(user.getIsDeleted()).isFalse();
	}

	@Test
	void 관리자_생성() {
		var admin = User.admin(
			"adminUser",
			UUID.randomUUID(),
			"password",
			"test",
			"test@test.com",
			"010-0000-0000",
			Gender.MALE,
			LocalDate.now()
		);

		assertThat(admin.getRole()).isEqualTo(Role.ADMIN);
		assertThat(admin.getStatus()).isEqualTo(Status.ACTIVE);
		assertThat(admin.getIsDeleted()).isFalse();
	}
}