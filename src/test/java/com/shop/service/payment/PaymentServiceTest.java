package com.shop.service.payment;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shop.domain.order.model.Order;
import com.shop.domain.order.model.OrderStatus;
import com.shop.domain.order.repository.OrderRepository;
import com.shop.domain.payment.model.Payment;
import com.shop.domain.payment.model.PaymentStatus;
import com.shop.domain.payment.model.PaymentType;
import com.shop.domain.payment.repository.PaymentRepository;
import com.shop.domain.payment.response.PaymentResponse;
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

	@Test
	void 결제_목록_조회_성공() {
		Long orderId = 1L;
		Order order = Mockito.mock(Order.class);

		Payment payment1 = Mockito.mock(Payment.class);
		Payment payment2 = Mockito.mock(Payment.class);
		List<Payment> payments = List.of(payment1, payment2);

		given(orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER)).willReturn(order);
		given(order.getPayments()).willReturn(payments);

		PaymentResponse response1 = Mockito.mock(PaymentResponse.class);
		PaymentResponse response2 = Mockito.mock(PaymentResponse.class);

		try (MockedStatic<PaymentResponse> mockedStatic = Mockito.mockStatic(PaymentResponse.class)) {
			mockedStatic.when(() -> PaymentResponse.of(payment1)).thenReturn(response1);
			mockedStatic.when(() -> PaymentResponse.of(payment2)).thenReturn(response2);

			var result = paymentService.getPayment(orderId);

			assertThat(result)
				.hasSize(2)
				.containsExactly(response1, response2);

			mockedStatic.verify(() -> PaymentResponse.of(payment1));
			mockedStatic.verify(() -> PaymentResponse.of(payment2));
		}
	}

	@Test
	void 결제_목록이_없는_경우() {
		Long orderId = 1L;
		Order order = Mockito.mock(Order.class);

		given(orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER)).willReturn(order);
		given(order.getPayments()).willReturn(List.of());

		var result = paymentService.getPayment(orderId);

		assertThat(result)
			.isNotNull()
			.isEmpty();
	}

	@Test
	void 결제_완료_성공() {
		Long paymentId = 1L;

		Payment payment = Mockito.mock(Payment.class);
		Order order = Mockito.mock(Order.class);

		given(paymentRepository.findByIdOrThrow(paymentId, ErrorCode.NOT_FOUND_PAYMENT)).willReturn(payment);
		given(payment.getOrder()).willReturn(order);
		given(payment.isPending()).willReturn(true);
		given(order.isPending()).willReturn(true);

		paymentService.completePayment(paymentId);

		verify(payment).complete();
		verify(order).completePayment();
	}

	@Test
	void 결제_대기가_아닌_경우_결제_완료_실패() {
		Long paymentId = 1L;

		Payment payment = Mockito.mock(Payment.class);
		Order order = Mockito.mock(Order.class);

		given(paymentRepository.findByIdOrThrow(paymentId, ErrorCode.NOT_FOUND_PAYMENT)).willReturn(payment);
		given(payment.getOrder()).willReturn(order);

		given(payment.isPending()).willReturn(false);

		assertThatThrownBy(() -> paymentService.completePayment(paymentId))
			.isInstanceOf(CustomException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.INVALID_PAYMENT_STATUS);

		verify(payment, never()).complete();
		verify(order, never()).completePayment();
	}

	@Test
	void 주문이_결제_대기가_아닌_경우_결제_완료_실패() {
		Long paymentId = 1L;

		Payment payment = Mockito.mock(Payment.class);
		Order order = Mockito.mock(Order.class);

		given(paymentRepository.findByIdOrThrow(paymentId, ErrorCode.NOT_FOUND_PAYMENT)).willReturn(payment);
		given(payment.getOrder()).willReturn(order);

		given(payment.isPending()).willReturn(true);
		given(order.isPending()).willReturn(false);

		assertThatThrownBy(() -> paymentService.completePayment(paymentId))
			.isInstanceOf(CustomException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.INVALID_ORDER_STATUS);

		verify(payment, never()).complete();
		verify(order, never()).completePayment();
	}

	@Test
	void 결제_실패_성공() {
		Long paymentId = 1L;

		Payment payment = Mockito.mock(Payment.class);
		Order order = Mockito.mock(Order.class);

		given(paymentRepository.findByIdOrThrow(paymentId, ErrorCode.NOT_FOUND_PAYMENT)).willReturn(payment);
		given(payment.getOrder()).willReturn(order);
		given(payment.isPending()).willReturn(true);
		given(order.isPending()).willReturn(true);

		paymentService.cancelPayment(paymentId);

		verify(payment).cancel();
		verify(order).resetToPending();
	}

	@Test
	void 결제_대기가_아닌_경우_결제_취소_실패() {
		Long paymentId = 1L;

		Payment payment = Mockito.mock(Payment.class);
		Order order = Mockito.mock(Order.class);

		given(paymentRepository.findByIdOrThrow(paymentId, ErrorCode.NOT_FOUND_PAYMENT)).willReturn(payment);
		given(payment.getOrder()).willReturn(order);

		given(payment.isPending()).willReturn(false);

		assertThatThrownBy(() -> paymentService.cancelPayment(paymentId))
			.isInstanceOf(CustomException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.INVALID_PAYMENT_STATUS);

		verify(payment, never()).cancel();
		verify(order, never()).resetToPending();
	}

	@Test
	void 주문이_결제_대기가_아닌_경우_결제_취소_실패() {
		Long paymentId = 1L;

		Payment payment = Mockito.mock(Payment.class);
		Order order = Mockito.mock(Order.class);

		given(paymentRepository.findByIdOrThrow(paymentId, ErrorCode.NOT_FOUND_PAYMENT)).willReturn(payment);
		given(payment.getOrder()).willReturn(order);

		given(payment.isPending()).willReturn(true);
		given(order.isPending()).willReturn(false);

		assertThatThrownBy(() -> paymentService.cancelPayment(paymentId))
			.isInstanceOf(CustomException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.INVALID_ORDER_STATUS);

		verify(payment, never()).cancel();
		verify(order, never()).resetToPending();
	}
}