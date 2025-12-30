package com.shop.payment.response;

import java.time.OffsetDateTime;

import com.shop.toss.response.TossPaymentsConfirmResponse;

public record PaymentConfirmResponse(
	String status,
	String paymentKey,
	String orderId,
	Long totalAmount,
	OffsetDateTime approvedAt,
	String method,
	String orderName
) {
	public static PaymentConfirmResponse of(TossPaymentsConfirmResponse res, String orderName) {
		return new PaymentConfirmResponse(
			res.status(),
			res.paymentKey(),
			res.orderId(),
			res.totalAmount(),
			res.approvedAt(),
			res.method(),
			orderName
		);
	}
}
