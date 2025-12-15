package com.kt.repository.review;

import static com.shop.domain.review.model.QAdminReview.*;
import static com.shop.domain.review.model.QReview.*;
import static com.shop.domain.user.model.QUser.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.domain.review.response.AdminNoReviewQueryResponse;
import com.shop.domain.review.response.AdminReviewDetailQueryResponse;
import com.shop.domain.review.response.QAdminNoReviewQueryResponse;
import com.shop.domain.review.response.QAdminReviewDetailQueryResponse;
import com.shop.domain.review.response.QAdminReviewQueryResponse;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AdminReviewRepositoryCustomImpl implements AdminReviewRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<AdminReviewDetailQueryResponse> findAllReviewsWithAdmin(Pageable pageable) {
		var query = jpaQueryFactory
			.select(new QAdminReviewDetailQueryResponse(
				review.id,
				review.title,
				review.content,
				user.uuid,
				new QAdminReviewQueryResponse(adminReview.title, adminReview.content) // 그냥 Q-type 그대로
			))
			.from(review)
			.join(review.user, user)
			.leftJoin(adminReview)
			.on(adminReview.review.eq(review)
				.and(adminReview.isDeleted.isFalse()))
			.where(review.isDeleted.isFalse());

		return query
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}


	@Override
	public List<AdminNoReviewQueryResponse> findReviewsWithoutAdmin(Pageable pageable) {
		var query = jpaQueryFactory
			.select(new QAdminNoReviewQueryResponse(
				review.id,
				review.title,
				review.content,
				user.uuid
			))
			.from(review)
			.join(review.user, user)
			.leftJoin(adminReview)
			.on(adminReview.review.eq(review).and(adminReview.isDeleted.isFalse()))
			.where(review.isDeleted.isFalse(), adminReview.id.isNull());

		return query
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
	}

	// 총 카운트
	@Override
	public long countAllReviews() {
		return jpaQueryFactory
			.select(review.count())
			.from(review)
			.where(review.isDeleted.isFalse())
			.fetchOne();
	}

	@Override
	public long countReviewsWithoutAdmin() {
		return jpaQueryFactory
			.select(review.count())
			.from(review)
			.leftJoin(adminReview)
			.on(adminReview.review.eq(review).and(adminReview.isDeleted.isFalse()))
			.where(review.isDeleted.isFalse(), adminReview.id.isNull())
			.fetchOne();
	}
}
