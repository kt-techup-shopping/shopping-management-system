package com.shop.repository.cartItem;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.domain.cart.QCart;
import com.shop.domain.cartItem.CartItem;
import com.shop.domain.cartItem.QCartItem;
import com.shop.domain.product.ProductStatus;
import com.shop.domain.product.QProduct;
import com.shop.repository.cartItem.response.CartItemQueryResponse;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CartItemRepositoryCustomImpl implements CartItemRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	private final QCartItem cartItem = QCartItem.cartItem;
	private final QProduct product = QProduct.product;
	private final QCart cart = QCart.cart;

	@Override
	public Page<CartItemQueryResponse> search(Long userId, String keyword, Pageable pageable) {
		List<CartItem> items = jpaQueryFactory
			.selectFrom(cartItem)
			.join(cartItem.product, product).fetchJoin()
			.join(cartItem.cart, cart).fetchJoin()
			.where(
				userIdEq(userId),
				productNameContains(keyword)
			)
			.orderBy(cartItem.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = jpaQueryFactory
			.select(cartItem.count())
			.from(cartItem)
			.join(cartItem.product, product)
			.join(cartItem.cart, cart)
			.where(
				userIdEq(userId),
				productNameContains(keyword)
			)
			.fetchOne();

		List<CartItemQueryResponse> content = items.stream()
			.map(CartItemQueryResponse::of)
			.toList();

		return new PageImpl<>(content, pageable, total != null ? total : 0);
	}

	public void deleteInactiveProducts() {
		jpaQueryFactory
			.delete(cartItem)
			.where(
				cartItem.product.status.in(ProductStatus.IN_ACTIVATED, ProductStatus.DELETED)
			)
			.execute();
	}

	private BooleanExpression userIdEq(Long userId) {
		return userId != null ? cart.user.id.eq(userId) : null;
	}

	private BooleanExpression productNameContains(String keyword) {
		return keyword != null && !keyword.trim().isEmpty() ?
			product.name.containsIgnoreCase(keyword) : null;
	}
}