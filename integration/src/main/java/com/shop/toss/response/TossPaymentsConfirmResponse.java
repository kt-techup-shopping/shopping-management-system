package com.shop.toss.response;

import java.time.OffsetDateTime;

public record TossPaymentsConfirmResponse(
	String status,
	String paymentKey,
	String orderId,
	Long totalAmount,
	OffsetDateTime approvedAt,
	String method
) {
}
