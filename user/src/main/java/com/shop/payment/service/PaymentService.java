package com.shop.payment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.ErrorCode;
import com.shop.Preconditions;
import com.shop.domain.payment.Payment;
import com.shop.domain.payment.PaymentType;
import com.shop.order.response.OrderCreatePaymentResponse;
import com.shop.payment.response.PaymentInfoResponse;
import com.shop.payment.response.PaymentResponse;
import com.shop.payment.vo.OrderId;
import com.shop.repository.order.OrderRepository;
import com.shop.repository.payment.PaymentRepository;
import com.shop.toss.TossPaymentsClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
	private final OrderRepository orderRepository;
	private final PaymentRepository paymentRepository;
	private final TossPaymentsClient tossPaymentsClient;

	public OrderCreatePaymentResponse createPayment(Long orderId, PaymentType type) {
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

		return OrderCreatePaymentResponse.from(payment);
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
		var payment = paymentRepository.findByIdOrThrow(paymentId, ErrorCode.NOT_FOUND_PAYMENT);
		var order = payment.getOrder();
		var orderId = OrderId.generate(order.getId(), paymentId);
		var orderName = order.generateOrderName();

		return PaymentInfoResponse.of(orderId, payment.getFinalAmount(), orderName);
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

		// 결제 상태 검증
		Preconditions.validate(payment.isPending(), ErrorCode.INVALID_PAYMENT_STATUS);

		// orderId 검증
		var parsed = OrderId.parse(orderId);
		Preconditions.validate(parsed.orderId().equals(order.getId()), ErrorCode.INVALID_ORDER_ID);
		Preconditions.validate(parsed.paymentId().equals(paymentId), ErrorCode.INVALID_ORDER_ID);

		// 결제 금액 검증
		Preconditions.validate(payment
			.getFinalAmount()
			.equals(amount), ErrorCode.INVALID_PAYMENT_AMOUNT);

		var toss = tossPaymentsClient.confirm(paymentKey, orderId, amount);

		// 토스 응답 확인
		Preconditions.validate("DONE".equals(toss.status()), ErrorCode.INVALID_PAYMENT_STATUS);
		Preconditions.validate(amount.equals(toss.totalAmount()), ErrorCode.INVALID_PAYMENT_AMOUNT);
		Preconditions.validate(orderId.equals(toss.orderId()), ErrorCode.INVALID_ORDER_ID);

		// 내부 완료 처리
		// TODO: 개선 필요할지도?
		payment.complete();
		order.completePayment();
	}
}
