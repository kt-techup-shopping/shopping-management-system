package com.shop.domain.user.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.shop.global.common.BaseEntity;
import com.shop.domain.order.model.Order;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class User extends BaseEntity {
	private String loginId;
	private UUID uuid;
	private String password;
	private String name;
	private String email;
	private String mobile;
	@Enumerated(EnumType.STRING)
	private Gender gender;
	private LocalDate birthday;
	@Enumerated(EnumType.STRING)
	private Role role;
	@Enumerated(EnumType.STRING)
	private Status status;

	@OneToMany(mappedBy = "user")
	private List<Order> orders = new ArrayList<>();

	public User(String loginId, UUID uuid, String password, String name, String email, String mobile, Gender gender,
		LocalDate birthday, LocalDateTime createdAt, LocalDateTime updatedAt, Role role, Status status) {
		this.loginId = loginId;
		this.uuid = uuid;
		this.password = password;
		this.name = name;
		this.email = email;
		this.mobile = mobile;
		this.gender = gender;
		this.birthday = birthday;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.status = status;
	}

	public static User normalUser(String loginId, UUID uuid, String password, String name, String email, String mobile,
		Gender gender,
		LocalDate birthday, LocalDateTime createdAt, LocalDateTime updatedAt, Status status) {
		return new User(
			loginId,
			uuid,
			password,
			name,
			email,
			mobile,
			gender,
			birthday,
			createdAt,
			updatedAt,
			Role.USER,
			status
		);
	}

	public static User admin(String loginId, String password, String name, String email, String mobile, Gender gender,
		LocalDate birthday, LocalDateTime createdAt, LocalDateTime updatedAt) {
		return User.admin(
			loginId,
			password,
			name,
			email,
			mobile,
			gender,
			birthday,
			createdAt,
			updatedAt
		);
	}

	public void changePassword(String password) {
		this.password = password;
	}

	public void update(String name, String email, String mobile) {
		this.name = name;
		this.email = email;
		this.mobile = mobile;
	}
}
