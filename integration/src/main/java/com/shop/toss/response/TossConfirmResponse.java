package com.shop.toss.response;

public record TossConfirmResponse(
	String paymentKey,
	String orderId,
	String status,
	Long totalAmount
) {}