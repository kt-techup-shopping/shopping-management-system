package com.shop.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.domain.payment.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
