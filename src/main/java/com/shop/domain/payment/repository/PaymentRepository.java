package com.shop.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.domain.payment.model.Payment;
import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	default Payment findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}
}
