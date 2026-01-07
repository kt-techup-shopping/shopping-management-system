package com.shop.repository.payment;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.domain.payment.PaymentStatus;
import com.shop.domain.payment.QPayment;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryCustomImpl implements PaymentRepositoryCustom {
	private final JPAQueryFactory jpaQueryFactory;
	private final QPayment payment = QPayment.payment;

	@Override
	public int markProcessingIfPending(Long paymentId) {
		return (int) jpaQueryFactory
			.update(payment)
			.set(payment.status, PaymentStatus.PROCESSING)
			.where(
				payment.id.eq(paymentId),
				payment.status.eq(PaymentStatus.PENDING)
			)
			.execute();
	}
}
