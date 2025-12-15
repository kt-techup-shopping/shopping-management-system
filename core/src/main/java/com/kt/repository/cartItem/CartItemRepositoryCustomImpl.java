package com.kt.repository.cartItem;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.domain.cart.model.QCart;
import com.shop.domain.cartitem.model.QCartItem;
import com.shop.domain.cartitem.response.CartItemResponse;
import com.shop.domain.product.model.ProductStatus;
import com.shop.domain.product.model.QProduct;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CartItemRepositoryCustomImpl implements CartItemRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	private final QCartItem cartItem = QCartItem.cartItem;
	private final QProduct product = QProduct.product;
	private final QCart cart = QCart.cart;

	@Override
	public Page<CartItemResponse> search(Long userId, String keyword, Pageable pageable) {
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

		List<CartItemResponse> content = items.stream()
			.map(CartItemResponse::of)
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