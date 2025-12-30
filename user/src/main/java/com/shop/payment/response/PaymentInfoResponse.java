package com.shop.payment.response;

import com.shop.domain.payment.Payment;

public record PaymentInfoResponse (
	String orderId,
	Long amount,
	String orderName
) {
	public static PaymentInfoResponse of(String orderId, Long amount, String orderName) {
		return new PaymentInfoResponse(
			orderId,
			amount,
			orderName
		);
	}
}
