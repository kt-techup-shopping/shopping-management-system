package com.shop.domain.review.model;

import com.shop.domain.orderproduct.model.OrderProduct;
import com.shop.domain.user.model.User;
import com.shop.global.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

	// title (제목) - VARCHAR(100)
	@NotNull
	@Column(length = 100)
	private String title;

	// content (내용) - VARCHAR(500)
	@NotNull
	@Column(length = 500)
	private String content;

	// order_product_id (주문 상품 ID) - BIGINT (Foreign Key)
	// OrderProduct 엔티티와의 다대일(N:1) 연관 관계 매핑
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_product_id", nullable = false)
	private OrderProduct orderProduct;

	// user_id (사용자 ID) - BIGINT (Foreign Key)
	// User 엔티티와의 다대일(N:1) 연관 관계 매핑
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public Review(String title, String content, OrderProduct orderProduct, User user){
		this.title = title;
		this.content = content;
		this.orderProduct = orderProduct;
		this.user = user;
	}

	public void update(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public void delete() {
		this.isDeleted = true;
	}

}
