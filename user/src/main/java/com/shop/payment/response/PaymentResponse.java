package com.shop.payment.response;

import com.shop.domain.payment.Payment;

public record PaymentResponse(
	Long id,
	Long totalAmount,
	Long discountAmount,
	Long deliveryFee,
	Long finalAmount,
	String status,
	String type
) {
	public static PaymentResponse of(Payment payment) {
		return new PaymentResponse(
			payment.getId(),
			payment.getTotalAmount(),
			payment.getDiscountAmount(),
			payment.getDeliveryFee(),
			payment.getFinalAmount(),
			payment
				.getStatus().name(),
			payment
				.getType().name()
		);
	}
}
