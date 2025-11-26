package com.shop.domain.user.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.shop.domain.review.model.Review;
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

	@OneToMany(mappedBy = "user")
	private List<Review> reviews = new ArrayList<>();


	public User(String loginId, UUID uuid, String password, String name, String email, String mobile, Gender gender,
		LocalDate birthday, Role role, Status status) {
		this.loginId = loginId;
		this.uuid = uuid;
		this.password = password;
		this.name = name;
		this.email = email;
		this.mobile = mobile;
		this.gender = gender;
		this.birthday = birthday;
		this.role = role;
		this.status = status;
	}

	public static User normalUser(String loginId, UUID uuid, String password, String name, String email, String mobile,
		Gender gender,
		LocalDate birthday) {
		return new User(
			loginId,
			uuid,
			password,
			name,
			email,
			mobile,
			gender,
			birthday,
			Role.USER,
			Status.ACTIVE
		);
	}

	public static User admin(String loginId, UUID uuid, String password, String name, String email, String mobile,
		Gender gender,
		LocalDate birthday) {
		return new User(
			loginId,
			uuid,
			password,
			name,
			email,
			mobile,
			gender,
			birthday,
			Role.ADMIN,
			Status.ACTIVE
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

	public void delete() {
		this.status = Status.INACTIVE;
		this.isDeleted = true;
	}

	public void promoteToAdmin() {
		this.role = Role.ADMIN;
	}

	public void demoteToUser() {
		this.role = Role.USER;
	}

	public void deactivate() {
		this.status = Status.INACTIVE;
	}
}
