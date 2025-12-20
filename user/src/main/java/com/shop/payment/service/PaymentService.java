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
// import com.shop.toss.TossPaymentsClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
	private final OrderRepository orderRepository;
	private final PaymentRepository paymentRepository;
	// private final TossPaymentsClient tossPaymentsClient;

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

	public void confirm(Long paymentId, String orderId, String paymentKey, Long amount) {
		var payment = paymentRepository.findByIdOrThrow(paymentId, ErrorCode.NOT_FOUND_PAYMENT);
		var order = payment.getOrder();

		System.out.println("결제 확인 요청 메서드까지 옴");

		// 결제 상태 검증
		Preconditions.validate(payment.isPending(), ErrorCode.INVALID_PAYMENT_STATUS);

		// orderId 검증
		// Preconditions.validate(order.getId());

		// 결제 금액 검증
		Preconditions.validate(payment
			.getFinalAmount()
			.equals(amount), ErrorCode.INVALID_PAYMENT_AMOUNT);

		// var toss = tossPaymentsClient.confirm(paymentKey, orderId, amount);
		//
		// Preconditions.validate("DONE".equals(toss.status()), ErrorCode.INVALID_PAYMENT_STATUS);
		// Preconditions.validate(amount.equals(toss.totalAmount()), ErrorCode.INVALID_PAYMENT_AMOUNT);
		// Preconditions.validate(orderId.equals(toss.orderId()), ErrorCode.INVALID_ORDER_ID);
		//
		// // 내부 완료 처리
		// System.out.println(toss);
	}
}
