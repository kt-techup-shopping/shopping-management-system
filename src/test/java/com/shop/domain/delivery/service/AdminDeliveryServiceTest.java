package com.shop.domain.delivery.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.shop.domain.delivery.model.Delivery;
import com.shop.domain.delivery.model.DeliveryStatus;
import com.shop.domain.delivery.repository.DeliveryRepository;
import com.shop.domain.delivery.request.DeliveryReadyRequest;
import com.shop.domain.delivery.response.DeliveryResponse;
import com.shop.domain.order.model.Order;
import com.shop.domain.order.repository.OrderRepository;
import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;

@ExtendWith(MockitoExtension.class)
class AdminDeliveryServiceTest {
	@InjectMocks
	private AdminDeliveryService adminDeliveryService;
	@Mock
	private DeliveryRepository deliveryRepository;
	@Mock
	private OrderRepository orderRepository;

	private Order order;

	private Delivery delivery;

	private String waybillNo1;
	private String waybillNo2;

	@BeforeEach
	void setUp() {
		order = new Order();
		delivery = new Delivery(order);
		waybillNo1 = "CJ-123-456-7890";
		waybillNo2 = "HJ-987-654-3210";

		ReflectionTestUtils.setField(order, "id", 1L);
		ReflectionTestUtils.setField(delivery, "id", 1L);
	}

	@Test
	void 배송상태_PENDING으로_변경_성공() {
		//given
		delivery.updateReady(waybillNo1);

		given(orderRepository.findByIdOrThrow(order.getId(), ErrorCode.NOT_FOUND_ORDER)).willReturn(order);
		given(deliveryRepository.findWithOrderByOrderIdOrThrow(order.getId(), ErrorCode.NOT_FOUND_DELIVERY))
			.willReturn(delivery);

		//when
		DeliveryResponse result = adminDeliveryService.updateToPending(order.getId());

		//then
		assertThat(result).isNotNull();
		assertThat(delivery.getDeliveryStatus()).isEqualTo(DeliveryStatus.PENDING);
		assertThat(delivery.getWaybillNo()).isNull();
	}

	@Test
	void 배송상태_READY로_변경_성공() {
		//given
		var request = new DeliveryReadyRequest(waybillNo1);

		given(orderRepository.findByIdOrThrow(order.getId(), ErrorCode.NOT_FOUND_ORDER)).willReturn(order);
		given(deliveryRepository.findWithOrderByOrderIdOrThrow(order.getId(), ErrorCode.NOT_FOUND_DELIVERY))
			.willReturn(delivery);

		//when
		DeliveryResponse result = adminDeliveryService.updateToReady(order.getId(), request);

		//then
		assertThat(result).isNotNull();
		assertThat(delivery.getDeliveryStatus()).isEqualTo(DeliveryStatus.READY);
		assertThat(delivery.getWaybillNo()).isEqualTo(waybillNo1);
	}

	@Test
	void 배송상태_READY로_변경_실패_송장번호_없음() {
		//given
		var request = new DeliveryReadyRequest(null);

		given(orderRepository.findByIdOrThrow(order.getId(), ErrorCode.NOT_FOUND_ORDER)).willReturn(order);
		given(deliveryRepository.findWithOrderByOrderIdOrThrow(order.getId(), ErrorCode.NOT_FOUND_DELIVERY))
			.willReturn(delivery);

		//then
		CustomException exception = assertThrowsExactly(CustomException.class, () ->
			adminDeliveryService.updateToReady(order.getId(), request)
		);
	}

	@Test
	void 배송상태_SHIPPING으로_변경_성공() {
		//given
		delivery.updateReady(waybillNo1);

		given(orderRepository.findByIdOrThrow(order.getId(), ErrorCode.NOT_FOUND_ORDER)).willReturn(order);
		given(deliveryRepository.findWithOrderByOrderIdOrThrow(order.getId(), ErrorCode.NOT_FOUND_DELIVERY))
			.willReturn(delivery);

		//when
		DeliveryResponse result = adminDeliveryService.updateToShipping(order.getId());

		//then
		assertThat(result).isNotNull();
		assertThat(delivery.getDeliveryStatus()).isEqualTo(DeliveryStatus.SHIPPING);
		assertThat(delivery.getWaybillNo()).isEqualTo(waybillNo1);
	}

	@Test
	void 배송상태_DELIVERED로_변경_성공() {
		//given
		delivery.updateReady(waybillNo1);

		given(orderRepository.findByIdOrThrow(order.getId(), ErrorCode.NOT_FOUND_ORDER)).willReturn(order);
		given(deliveryRepository.findWithOrderByOrderIdOrThrow(order.getId(), ErrorCode.NOT_FOUND_DELIVERY))
			.willReturn(delivery);

		//when
		DeliveryResponse result = adminDeliveryService.updateToDelivered(order.getId());

		//then
		assertThat(result).isNotNull();
		assertThat(delivery.getDeliveryStatus()).isEqualTo(DeliveryStatus.DELIVERED);
		assertThat(delivery.getWaybillNo()).isEqualTo(waybillNo1);
	}

	@Test
	void 배송상태_변경_실패__주문_없음() {
		//given
		Long orderId = 999L;

		given(orderRepository.findByIdOrThrow(eq(orderId), eq(ErrorCode.NOT_FOUND_ORDER)))
			.willThrow(new CustomException(ErrorCode.NOT_FOUND_ORDER));

		//then
		assertThrowsExactly(CustomException.class, () ->
			adminDeliveryService.updateToPending(orderId)
		);
	}

	@Test
	void 배송상태_변경_실패__배송_없음() {
		//given
		given(orderRepository.findByIdOrThrow(eq(order.getId()), eq(ErrorCode.NOT_FOUND_ORDER))).willReturn(order);
		given(deliveryRepository.findWithOrderByOrderIdOrThrow(eq(order.getId()), eq(ErrorCode.NOT_FOUND_DELIVERY)))
			.willThrow(new CustomException(ErrorCode.NOT_FOUND_DELIVERY));

		//then
		assertThrowsExactly(CustomException.class, () ->
			adminDeliveryService.updateToPending(order.getId())
		);
	}

	@Test
	void 송장번호_변경_테스트() {
		//given
		delivery.updateReady(waybillNo1);

		given(orderRepository.findByIdOrThrow(order.getId(), ErrorCode.NOT_FOUND_ORDER)).willReturn(order);
		given(deliveryRepository.findWithOrderByOrderIdOrThrow(order.getId(), ErrorCode.NOT_FOUND_DELIVERY)).willReturn(
			delivery);

		//when
		var request2 = new DeliveryReadyRequest(waybillNo2);
		DeliveryResponse result = adminDeliveryService.updateToReady(order.getId(), request2);

		//then
		assertThat(result).isNotNull();
		assertThat(delivery.getDeliveryStatus()).isEqualTo(DeliveryStatus.READY);
		assertThat(delivery.getWaybillNo()).isEqualTo(waybillNo2);
	}
}