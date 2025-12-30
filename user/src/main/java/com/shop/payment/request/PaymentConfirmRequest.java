package com.shop.payment.request;

public record PaymentConfirmRequest (
	String paymentKey,
	String orderId,
	Long amount
) {
}
