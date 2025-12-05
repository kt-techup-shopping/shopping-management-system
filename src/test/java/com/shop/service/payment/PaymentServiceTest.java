package com.shop.service.payment;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shop.domain.order.model.Order;
import com.shop.domain.order.repository.OrderRepository;
import com.shop.domain.payment.model.Payment;
import com.shop.domain.payment.model.PaymentStatus;
import com.shop.domain.payment.model.PaymentType;
import com.shop.domain.payment.repository.PaymentRepository;
import com.shop.domain.payment.service.PaymentService;
import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private PaymentRepository paymentRepository;

	@InjectMocks
	private PaymentService paymentService;

	@Test
	void 결제_생성_성공() {
		Long orderId = 1L;
		PaymentType type = PaymentType.CARD;
		Long totalAmount = 100_000L;
		Order order = Mockito.mock(Order.class);

		given(orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER)).willReturn(order);
		given(order.isCompleted()).willReturn(false);
		given(order.canRequestPayment()).willReturn(true);
		given(order.calculateTotalAmount()).willReturn(totalAmount);

		paymentService.createPayment(orderId, type);

		ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
		verify(paymentRepository).save(paymentCaptor.capture());

		Payment payment = paymentCaptor.getValue();

		assertThat(payment.getFinalAmount()).isEqualTo(totalAmount);
		assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
		assertThat(payment.getOrder()).isEqualTo(order);

		verify(order).addPayment(payment);
	}

	@Test
	void 이미_결제완료된_주문_결제_실패() {
		Long orderId = 1L;
		PaymentType type = PaymentType.CARD;
		Order order = Mockito.mock(Order.class);

		given(orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER)).willReturn(order);
		given(order.isCompleted()).willReturn(true);

		assertThatThrownBy(() -> paymentService.createPayment(orderId, type))
			.isInstanceOf(CustomException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.ALREADY_PAID_ORDER);
	}

	@Test
	void 이미_결제_대기중인_주문_결제_실패() {
		Long orderId = 1L;
		PaymentType type = PaymentType.ACCOUNT_TRANSFER;
		Long totalAmount = 100_000L;
		Order order = Mockito.mock(Order.class);

		given(orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER)).willReturn(order);
		given(order.isCompleted()).willReturn(false);
		given(order.canRequestPayment()).willReturn(true, false);
		given(order.calculateTotalAmount()).willReturn(totalAmount);

		paymentService.createPayment(orderId, type);

		assertThatThrownBy(() -> paymentService.createPayment(orderId, type))
			.isInstanceOf(CustomException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.ALREADY_PENDING_ORDER);

		verify(paymentRepository, Mockito.times(1)).save(Mockito.any(Payment.class));
		verify(order, Mockito.times(1)).addPayment(Mockito.any(Payment.class));
	}
}