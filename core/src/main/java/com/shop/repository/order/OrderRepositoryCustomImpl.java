package com.shop.repository.order;

import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.domain.order.OrderStatus;
import com.shop.repository.order.response.AdminOrderDetailQueryResponse;
import com.shop.repository.order.response.AdminOrderDetailUserQueryResponse;
import com.shop.repository.order.response.OrderDetailQueryResponse;
import com.shop.repository.order.response.OrderDetailUserQueryResponse;
import com.shop.repository.order.response.OrderResponse;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {
	// QClass를 임포트해서 쿼리를 작성할 때 사용하면 됩니다.
	private final JPAQueryFactory jpaQueryFactory;
	private final QOrder order = QOrder.order;
	private final QOrderProduct orderProduct = QOrderProduct.orderProduct;
	private final QProduct product = QProduct.product;
	private final QPayment payment = QPayment.payment;

	// @Override
	// public Page<OrderResponse.Search> search(
	// 	String keyword,
	// 	Pageable pageable
	// ) {
	// 	// 페이징을 구현할때
	// 	// offset, limit
	// 	// select *
	// 	// booleanBuilder, BooleanExpression
	// 	var booleanBuilder = new BooleanBuilder();
	//
	// 	booleanBuilder.and(containsProductName(keyword));
	//
	// 	// booleanBuilder.and()
	// 	// booleanBuilder.or()
	// 	// booleanBuilder안에다가 booleanExpression을 추가해주는 방식으로
	//
	// 	var content = jpaQueryFactory
	// 		// order자리에 QOrderResponse_Search
	// 		// totalPrice는 product의 price * orderProduct.quantity
	// 		.select(new QOrderResponse_Search(
	// 			order.id,
	// 			order.receiver.name,
	// 			product.name,
	// 			orderProduct.quantity,
	// 			product.price.multiply(orderProduct.quantity),
	// 			order.status,
	// 			order.createdAt
	// 		))
	// 		.from(order)
	// 		.join(orderProduct).on(orderProduct.order.id.eq(order.id))
	// 		.join(product).on(orderProduct.product.id.eq(product.id))
	// 		.where(booleanBuilder)
	// 		.orderBy(order.id.desc())
	// 		.offset(pageable.getOffset())
	// 		.limit(pageable.getPageSize())
	// 		.fetch();
	// 	// 최초에 페이지 접근했을때 -> 전체검색이 되야할까 아니면 특정키워드검색이 자동으로 되야하나
	// 	// name like '%null%' (동작 해야하나?) - 동작안해야
	// 	// keyword = null
	//
	// 	var total = (long)jpaQueryFactory.select(order.id)
	// 		.from(order)
	// 		.join(orderProduct).on(orderProduct.order.id.eq(order.id))
	// 		.join(product).on(orderProduct.product.id.eq(product.id))
	// 		.where(booleanBuilder)
	// 		.fetch().size();
	//
	// 	return new PageImpl<>(content, pageable, total);
	// }

	@Override
	public List<OrderDetailQueryResponse> findOrderDetailByUserId(Long userId) {
		return jpaQueryFactory
			.select(new QOrderDetailQueryResponse(
				order.id,
				order.receiver.name,
				order.receiver.address,
				order.receiver.mobile,
				product.name,
				product.price,
				orderProduct.quantity,
				order.status.stringValue(),
				order.deliveredAt
			))
			.from(order)
			.join(orderProduct).on(orderProduct.order.eq(order)
				.and(orderProduct.isDeleted.eq(false))
			)
			.join(product).on(orderProduct.product.eq(product))
			.where(
				order.user.id.eq(userId),
				order.isDeleted.eq(false)
			)
			.orderBy(order.createdAt.desc())
			.fetch();
	}

	@Override
	public Page<AdminOrderDetailQueryResponse> findAdminOrderDetail(
		Long orderId, Long userId, OrderStatus status, PageRequest pageable
	) {
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(filterOrderId(orderId));
		builder.and(filterUserId(userId));
		builder.and(filterStatus(status));

		var content = jpaQueryFactory
			.select(new QAdminOrderDetailQueryResponse(
				order.id,
				order.user.id,
				order.receiver.name,
				order.receiver.address,
				order.receiver.mobile,
				product.name,
				product.price,
				orderProduct.quantity,
				order.status.stringValue(),
				order.deliveredAt
			))
			.from(order)
			.join(orderProduct).on(orderProduct.order.eq(order))
			.join(product).on(orderProduct.product.eq(product))
			.where(builder)
			.orderBy(order.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		var total = jpaQueryFactory
			.select(order.id.countDistinct())
			.from(order)
			.join(orderProduct).on(orderProduct.order.eq(order))
			.join(product).on(orderProduct.product.eq(product))
			.where(builder)
			.fetchOne();
		var totalCount = (total != null) ? total : 0L;

		return new PageImpl<>(content, pageable, totalCount);
	}

	// 내 주문 상세 조회
	@Override
	public List<OrderDetailUserQueryResponse> findOrderDetailByUserIdAndOrderId(Long userId, Long orderId) {

		QPayment paymentSub = new QPayment("paymentSub");

		// 서브쿼리: 해당 주문의 최신 결제 ID
		var latestPaymentIdSubQuery = JPAExpressions
			.select(paymentSub.id.max())
			.from(paymentSub)
			.where(paymentSub.order.eq(order));

		return jpaQueryFactory
			.select(new QOrderDetailUserQueryResponse(
				order.id,
				order.receiver.name,
				order.receiver.address,
				order.receiver.mobile,
				order.status,
				order.deliveredAt,
				order.createdAt,

				product.id,
				product.name,
				product.price,
				orderProduct.quantity,

				payment.totalAmount,
				payment.deliveryFee,
				payment.type
			))
			.from(order)
			.join(orderProduct).on(orderProduct.order.eq(order)
				.and(orderProduct.isDeleted.eq(false)))
			.join(product).on(orderProduct.product.eq(product))
			.leftJoin(payment).on(
				payment.id.eq(latestPaymentIdSubQuery)
			)
			.where(
				order.id.eq(orderId),
				order.user.id.eq(userId),
				order.isDeleted.eq(false)
			)
			.orderBy(orderProduct.id.asc())
			.fetch();
	}

	@Override
	public List<AdminOrderDetailUserQueryResponse> findAdminOrderDetailUserById(Long orderId) {
		return jpaQueryFactory
			.select(new QAdminOrderDetailUserQueryResponse(
				order.id,
				order.user.id,
				order.user.name,
				order.user.email,
				order.user.mobile,
				order.receiver.name,
				order.receiver.address,
				order.receiver.mobile,
				order.status,
				order.deliveredAt,
				order.createdAt,

				product.id,
				product.name,
				product.price,
				orderProduct.quantity,

				payment.totalAmount,
				payment.deliveryFee,
				payment.type
			))
			.from(order)
			.join(orderProduct).on(orderProduct.order.eq(order)
				.and(orderProduct.isDeleted.eq(false)))
			.join(product).on(orderProduct.product.eq(product))
			.join(payment).on(payment.order.eq(order))
			.where(
				order.id.eq(orderId),
				order.isDeleted.eq(false)
			)
			.orderBy(orderProduct.id.asc())
			.fetch();
	}

	// 시작하는 '%keyword'
	// 끝나는 'keyword%'
	// 포함하는 '%" "%'
	// 공백이면 어쩌징

	// Strings
	// Objects
	private BooleanExpression containsProductName(String keyword) {
		// if(Strings.isNotBlank(keyword)) {
		// 	return product.name.containsIgnoreCase(keyword);
		// } else {
		// 	return null;
		// }
		return Strings.isNotBlank(keyword) ? product.name.containsIgnoreCase(keyword) : null;
	}

	private BooleanExpression filterOrderId(Long orderId) {
		return orderId != null ? order.id.eq(orderId) : null;
	}

	private BooleanExpression filterUserId(Long userId) {
		return userId != null ? order.user.id.eq(userId) : null;
	}

	private BooleanExpression filterStatus(OrderStatus status) {
		return status != null ? order.status.eq(status) : null;
	}

}
