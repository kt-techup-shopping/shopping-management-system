package com.shop.order.response;

import com.shop.domain.payment.Payment;

public record OrderCreatePaymentResponse (
	Long paymentId
) {
	public static OrderCreatePaymentResponse from(Payment payment) {
		return new OrderCreatePaymentResponse(
			payment.getId()
		);
	}
}
