package com.shop.repository.payment;

public interface PaymentRepositoryCustom {
	int markProcessingIfPending(Long paymentId);
}
