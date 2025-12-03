package com.shop.domain.user.model;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

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

	@Test
	void 비밀번호_변경() {
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

		String newPassword = "newPassword";
		user.changePassword(newPassword);

		assertThat(user.getPassword()).isEqualTo(newPassword);
	}

	@Test
	void 유저_정보_수정() {
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

		String newName = "newName";
		String newEmail = "newEmail";
		String newMobile = "010-1111-1111";

		user.update(newName, newEmail, newMobile);

		assertThat(user.getName()).isEqualTo(newName);
		assertThat(user.getEmail()).isEqualTo(newEmail);
		assertThat(user.getMobile()).isEqualTo(newMobile);
	}

	@Test
	void 유저_삭제() {
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

		user.delete();

		assertThat(user.getIsDeleted()).isTrue();
		assertThat(user.getStatus()).isEqualTo(Status.INACTIVE);
	}

	@Test
	void 유저_비활성화() {
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

		user.deactivate();

		assertThat(user.getIsDeleted()).isFalse();
		assertThat(user.getStatus()).isEqualTo(Status.INACTIVE);
	}

	@Test
	void 관리자_권한_삭제() {
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

		admin.demoteToUser();

		assertThat(admin.getRole()).isEqualTo(Role.USER);
	}
}