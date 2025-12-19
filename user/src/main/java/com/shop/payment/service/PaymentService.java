package com.shop.payment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.ErrorCode;
import com.shop.Preconditions;
import com.shop.domain.payment.Payment;
import com.shop.domain.payment.PaymentType;
import com.shop.payment.response.PaymentInfoResponse;
import com.shop.payment.response.PaymentResponse;
import com.shop.repository.order.OrderRepository;
import com.shop.repository.payment.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
	private final OrderRepository orderRepository;
	private final PaymentRepository paymentRepository;

	public void createPayment(Long orderId, PaymentType type) {
		var order = orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER);
		Preconditions.validate(!order.isCompleted(), ErrorCode.ALREADY_PAID_ORDER);
		Preconditions.validate(order.canRequestPayment(), ErrorCode.ALREADY_PENDING_ORDER);

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

	public PaymentInfoResponse getPaymentInfo(Long paymentId) {
		final String ORD_PREFIX = "ORD";

		var payment = paymentRepository.findByIdOrThrow(paymentId, ErrorCode.NOT_FOUND_PAYMENT);
		var order = payment.getOrder();

		var orderId = String.format(
			"%s_%06d_%06d",
			ORD_PREFIX,
			order.getId(),
			paymentId
		);

		return PaymentInfoResponse.of(orderId, payment.getFinalAmount(), "주문 결제");
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

		Preconditions.validate(payment.isPending(), ErrorCode.INVALID_PAYMENT_STATUS);
		Preconditions.validate(order.isPending(), ErrorCode.INVALID_ORDER_STATUS);

		payment.cancel();
		order.resetToPending();
	}
}
