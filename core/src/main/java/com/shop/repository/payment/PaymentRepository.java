package com.shop.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.CustomException;
import com.shop.ErrorCode;
import com.shop.domain.payment.Payment;

public interface PaymentRepository extends PaymentRepositoryCustom,  JpaRepository<Payment, Long> {
	default Payment findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}
}
