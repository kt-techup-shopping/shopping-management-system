package com.shop.domain.payment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.order.repository.OrderRepository;
import com.shop.domain.payment.model.Payment;
import com.shop.domain.payment.model.PaymentType;
import com.shop.domain.payment.repository.PaymentRepository;
import com.shop.domain.payment.response.PaymentResponse;
import com.shop.global.common.ErrorCode;
import com.shop.global.common.Preconditions;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
	private final OrderRepository orderRepository;
	private final PaymentRepository paymentRepository;

	public void createPayment(Long orderId, PaymentType type) {
		var order = orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER);

		Preconditions.validate(order.canRequestPayment(), ErrorCode.INVALID_ORDER_STATUS);

		Long totalAmount = order.calculateTotalAmount();
		// TODO: 쿠폰이나 멤버쉽 구현 이후 적용
		Long discountAmount = 0L;
		Long deliveryFee = 0L;

		var payment = Payment.create(
			totalAmount,
			discountAmount,
			deliveryFee,
			type,
			order
		);

		order.addPayment(payment);


		paymentRepository.save(payment);
	}

	public List<PaymentResponse> getPayment(Long orderId) {
		var order = orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER);

		return order
			.getPayments()
			.stream()
			.map(PaymentResponse::of)
			.toList();
	}

	public void completePayment(Long paymentId) {
		var payment = paymentRepository.findByIdOrThrow(paymentId, ErrorCode.NOT_FOUND_PAYMENT);
		var order = payment.getOrder();

		Preconditions.validate(payment.isPending(), ErrorCode.INVALID_PAYMENT_STATUS);
		Preconditions.validate(order.isPending(), ErrorCode.INVALID_ORDER_STATUS);

		payment.complete();
		order.completePayment();
	}

	public void cancelPayment(Long paymentId) {
		var payment = paymentRepository.findByIdOrThrow(paymentId, ErrorCode.NOT_FOUND_PAYMENT);
		var order = payment.getOrder();

		Preconditions.validate(payment.canCancel(), ErrorCode.INVALID_PAYMENT_STATUS);
		Preconditions.validate(order.isPending(), ErrorCode.INVALID_ORDER_STATUS);

		payment.cancel();
		order.resetToPending();
	}
}
