package com.kt.repository.review;

import static com.shop.domain.orderproduct.model.QOrderProduct.*;
import static com.shop.domain.product.model.QProduct.*;
import static com.shop.domain.review.model.QAdminReview.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.domain.review.model.QReview;
import com.shop.domain.review.model.QReviewLike;
import com.shop.domain.review.model.ReviewLikeType;
import com.shop.domain.review.response.QAdminReviewQueryResponse;
import com.shop.domain.review.response.QReviewDetailQueryResponse;
import com.shop.domain.review.response.QReviewPageQueryResponse;
import com.shop.domain.review.response.ReviewDetailQueryResponse;
import com.shop.domain.review.response.ReviewPageQueryResponse;
import com.shop.domain.user.model.QUser;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	private final QReview review = QReview.review;
	private final QReviewLike reviewLike = QReviewLike.reviewLike;
	private final QUser user = QUser.user;

	/**
	 * 특정 상품에 대한 리뷰를 조회
	 * 좋아요순, 최신순, 오래된순(기본값) 에 대한 정렬 가능
	 * 페이지네이션 적용
	 */
	@Override
	public List<ReviewPageQueryResponse> findReviews(
		Long loginUserId,
		Long productId,
		int offset,
		int limit,
		String sort
	) {
		var reviewLikeTypeExpr = (loginUserId == null)
			? Expressions.constant(ReviewLikeType.NONE)
			: Expressions.cases()
			.when(reviewLike.id.isNotNull()).then(reviewLike.reviewLikeType)
			.otherwise(ReviewLikeType.NONE);

		StringTemplate content250 = Expressions.stringTemplate(
			"SUBSTRING({0}, 1, 250)", review.content
		);

		var query = jpaQueryFactory
			.select(new QReviewPageQueryResponse(
				review.id,
				review.title,
				content250,
				review.orderProduct.id,
				user.uuid,
				review.likeCount,
				review.dislikeCount,
				reviewLikeTypeExpr,
				new QAdminReviewQueryResponse(adminReview.title, adminReview.content)
			))
			.from(review)
			.join(review.user, user)
			.join(review.orderProduct, orderProduct)
			.join(orderProduct.product, product)
			.leftJoin(reviewLike)
			.on(
				reviewLike.review.eq(review)
					.and(loginUserId != null ? reviewLike.user.id.eq(loginUserId) : Expressions.FALSE)
					.and(reviewLike.isDeleted.isFalse())
			)
			.leftJoin(adminReview)
			.on(
				adminReview.review.eq(review)
					.and(adminReview.isDeleted.isFalse())
			)
			.where(
				review.isDeleted.isFalse(),
				productId != null ? product.id.eq(productId) : null
			)
			.offset(offset)
			.limit(limit);

		// 정렬
		switch (sort.toLowerCase()) {
			case "latest":
				query.orderBy(review.id.desc());
				break;
			case "likes":
				query.orderBy(review.likeCount.desc());
				break;
			default:
				query.orderBy(review.id.asc());
		}

		return query.fetch();
	}


	/**
	 * 특정 사용자 리뷰 조회하는
	 * 페이지네이션 적용
	 */
	@Override
	public List<ReviewPageQueryResponse> findReviewsByUser(Long targetUserId, Long loginUserId, int offset, int limit) {

		var reviewLikeTypeExpr = (loginUserId == null)
			? Expressions.constant(ReviewLikeType.NONE)
			: Expressions.cases()
			.when(reviewLike.id.isNotNull()).then(reviewLike.reviewLikeType)
			.otherwise(ReviewLikeType.NONE);

		StringTemplate content250 = Expressions.stringTemplate(
			"SUBSTRING({0}, 1, 250)", review.content
		);

		return jpaQueryFactory
			.select(new QReviewPageQueryResponse(
				review.id,
				review.title,
				content250,
				review.orderProduct.id,
				user.uuid,
				review.likeCount,
				review.dislikeCount,
				reviewLikeTypeExpr,
				new QAdminReviewQueryResponse(adminReview.title, adminReview.content)
			))
			.from(review)
			.join(review.user, user)
			.leftJoin(reviewLike)
			.on(
				reviewLike.review.eq(review)
					.and(loginUserId != null ? reviewLike.user.id.eq(loginUserId) : Expressions.FALSE)
					.and(reviewLike.isDeleted.isFalse())
			)
			.leftJoin(adminReview)
			.on(
				adminReview.review.eq(review)
					.and(adminReview.isDeleted.isFalse())
			)
			.where(
				review.user.id.eq(targetUserId),
				review.isDeleted.isFalse()
			)
			.offset(offset)
			.limit(limit)
			.orderBy(review.id.desc())
			.fetch();
	}

	/**
	 * 단일 리뷰 조회하는
	 */
	@Override
	public ReviewDetailQueryResponse findReviewById(Long reviewId, Long loginUserId) {

		var likeTypeExpression = (loginUserId == null)
			? Expressions.constant(ReviewLikeType.NONE)
			: Expressions.cases()
			.when(reviewLike.id.isNotNull()).then(reviewLike.reviewLikeType)
			.otherwise(ReviewLikeType.NONE);

		return jpaQueryFactory
			.select(
				new QReviewDetailQueryResponse(
					review.id,
					review.title,
					review.content,
					review.orderProduct.id,
					user.uuid,
					review.likeCount,
					review.dislikeCount,
					likeTypeExpression,
					new QAdminReviewQueryResponse(adminReview.title, adminReview.content)
				)
			)
			.from(review)
			.join(review.user, user)
			.leftJoin(reviewLike)
			.on(
				reviewLike.review.eq(review)
					.and(loginUserId != null
						? reviewLike.user.id.eq(loginUserId)
						: Expressions.FALSE)
					.and(reviewLike.isDeleted.isFalse())
			)
			.leftJoin(adminReview)
			.on(
				adminReview.review.eq(review)
					.and(adminReview.isDeleted.isFalse())
			)
			.where(
				review.id.eq(reviewId),
				review.isDeleted.isFalse()
			)
			.fetchOne();
	}

	@Override
	public long countReviews() {
		Long count = jpaQueryFactory
			.select(review.count())
			.from(review)
			.where(review.isDeleted.isFalse())
			.fetchOne();

		return count != null ? count : 0;
	}

	@Override
	public long countReviewsByUser(Long userId) {
		Long count = jpaQueryFactory
			.select(review.count())
			.from(review)
			.where(
				review.user.id.eq(userId),
				review.isDeleted.isFalse()
			)
			.fetchOne();

		return count != null ? count : 0;
	}

	// 상품에 따른 총 개수 조회
	@Override
	public long countReviewsByProduct(Long productId) {
		Long count = jpaQueryFactory
			.select(review.count())
			.from(review)
			.where(
				review.orderProduct.id.eq(productId),
				review.isDeleted.isFalse()
			)
			.fetchOne();

		return count != null ? count : 0L;
	}


}