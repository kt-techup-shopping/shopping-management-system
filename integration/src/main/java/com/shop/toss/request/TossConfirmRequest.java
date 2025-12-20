package com.shop.toss.request;

public record TossConfirmRequest(
	String paymentKey,
	String orderId,
	Long amount
) {}
