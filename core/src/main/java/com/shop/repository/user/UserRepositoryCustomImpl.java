package com.shop.repository.user;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.domain.user.Gender;
import com.shop.domain.user.QUser;
import com.shop.domain.user.Role;
import com.shop.domain.user.Status;
import com.shop.repository.user.response.QUserSearchQueryResponse;
import com.shop.repository.user.response.UserSearchQueryResponse;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;
	private final QUser user = QUser.user;

	@Override
	public Page<UserSearchQueryResponse> search(
		String keyword,
		Gender gender,
		Boolean activeOnly,
		Role role,
		String sort,
		PageRequest pageable
	) {
		var booleanBuilder = new BooleanBuilder();
		booleanBuilder.and(containsUserName(keyword));
		booleanBuilder.and(filterGender(gender));
		booleanBuilder.and(filterActive(activeOnly));
		booleanBuilder.and(filterRole(role));

		var content = jpaQueryFactory
			.select(new QUserSearchQueryResponse(
				user.id,
				user.loginId,
				user.name,
				user.email,
				user.mobile,
				user.gender,
				user.status
			))
			.from(user)
			.where(booleanBuilder)
			.orderBy(resolveSort(sort))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		var total = jpaQueryFactory
			.select(user.id.count())
			.from(user)
			.where(booleanBuilder)
			.fetchOne();

		return new PageImpl<>(content, pageable, total != null ? total : 0);
	}

	// 이름 검색
	private BooleanBuilder containsUserName(String keyword) {
		var builder = new BooleanBuilder();
		if (Strings.isNotBlank(keyword)) {
			builder.and(
				user.name.containsIgnoreCase(keyword)
					.or(user.loginId.containsIgnoreCase(keyword))
			);
		}
		return builder;
	}

	// 성별 필터링
	private BooleanBuilder filterGender(Gender gender) {
		var builder = new BooleanBuilder();
		if (gender != null) {
			builder.and(user.gender.eq(gender));
		}
		return builder;
	}

	// 활성/비활성 필터링
	private BooleanBuilder filterActive(Boolean activeOnly) {
		var builder = new BooleanBuilder();
		if (Boolean.TRUE.equals(activeOnly)) {
			builder.and(user.status.eq(Status.ACTIVE));
		}
		return builder;
	}

	// Role 필터링
	private BooleanBuilder filterRole(Role role) {
		var builder = new BooleanBuilder();
		if (role != null) {
			builder.and(user.role.eq(role));
		}
		return builder;
	}

	// 정렬 기준
	private OrderSpecifier<?> resolveSort(String sort) {
		if (Strings.isBlank(sort)) {
			return user.id.desc();
		}

		return switch (sort) {
			case "nameAsc" -> user.name.asc();
			case "nameDesc" -> user.name.desc();
			case "latest" -> user.createdAt.desc();
			case "oldest" -> user.createdAt.asc();
			default -> user.id.desc();
		};
	}
}
