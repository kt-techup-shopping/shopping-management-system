package com.shop.domain.payment.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.shop.domain.order.model.Order;
import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;

class PaymentTest {

	@Test
	void 결제_생성() {
		Long totalAmount = 100_000L;
		Long discountAmount = 10_000L;
		Long deliverryFee = 3_000L;
		PaymentType type = PaymentType.CARD;
		Order order = Mockito.mock(Order.class);

		var payment = Payment.create(totalAmount, discountAmount, deliverryFee, type, order);

		Long expectedFinalAmount = totalAmount - discountAmount + deliverryFee;

		assertThat(payment).isNotNull();
		assertThat(payment.getFinalAmount()).isEqualTo(expectedFinalAmount);
		assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
	}

	@Test
	void 주문이_없는_경우_실패() {
		Long totalAmount = 100_000L;
		Long discountAmount = 10_000L;
		Long deliverryFee = 3_000L;
		PaymentType type = PaymentType.CARD;
		Order order = null;

		assertThatThrownBy(() -> Payment.create(totalAmount, discountAmount, deliverryFee, type, order))
			.isInstanceOf(CustomException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.REQUIRED_ORDER_FOR_PAYMENT);
	}

	@Test
	void 결제_완료() {
		var payment = Payment.create(10_000L, 0L, 0L, PaymentType.CARD, Mockito.mock(Order.class));

		assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);

		payment.complete();

		assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
	}

	@Test
	void 결제_취소() {
		var payment = Payment.create(10_000L, 0L, 0L, PaymentType.CARD, Mockito.mock(Order.class));

		assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);

		payment.cancel();

		assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELED);
	}
}