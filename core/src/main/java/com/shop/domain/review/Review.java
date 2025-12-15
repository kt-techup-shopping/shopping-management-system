package com.shop.domain.review;

import java.util.ArrayList;
import java.util.List;

import com.shop.domain.orderproduct.model.OrderProduct;
import com.shop.domain.user.model.User;
import com.shop.global.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

	// 제목
	@NotNull
	@Column(length = 100)
	private String title;

	// 내용
	@NotNull
	@Column(length = 500)
	private String content;

	// 주문 상품
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_product_id")
	private OrderProduct orderProduct;

	// 작성자
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	// 좋아요/싫어요 관계
	@OneToMany(mappedBy = "review")
	private List<ReviewLike> reviewLikes = new ArrayList<>();

	// 좋아요/싫어요 관계
	@OneToMany(mappedBy = "review")
	private List<AdminReview> adminReviews = new ArrayList<>();

	// 좋아요/싫어요 수
	@NotNull
	private int likeCount = 0;

	@NotNull
	private int dislikeCount = 0;

	// 낙관적 락용
	@Version
	private int version;

	public Review(String title, String content, OrderProduct orderProduct, User user) {
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

	public void incrementLike() {
		this.likeCount++;
	}

	public void decrementLike() {
		if (this.likeCount > 0) this.likeCount--;
	}

	public void incrementDislike() {
		this.dislikeCount++;
	}

	public void decrementDislike() {
		if (this.dislikeCount > 0) this.dislikeCount--;
	}

}
