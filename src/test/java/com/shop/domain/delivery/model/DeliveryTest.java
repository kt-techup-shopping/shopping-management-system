package com.shop.domain.delivery.model;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import com.shop.domain.order.model.Order;
import com.shop.global.common.CustomException;

class DeliveryTest {
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
	}

	@Test
	void 배송_생성_성공() {

		//then
		assertThat(delivery.getOrder()).isEqualTo(order);
		assertThat(delivery.getDeliveryStatus()).isEqualTo(DeliveryStatus.PENDING);
		assertThat(delivery.getWaybillNo()).isNull();
	}

	@Test
	void 배송_상태_PENDING으로_변경() {

		//given
		delivery.updateReady(waybillNo1); // 먼저 READY 상태로 변경

		//when
		delivery.updatePending();

		//then
		assertThat(delivery.getDeliveryStatus()).isEqualTo(DeliveryStatus.PENDING);
		assertThat(delivery.getWaybillNo()).isNull();
	}

	@Test
	void 배송_상태_READY로_변경() {

		//when
		delivery.updateReady(waybillNo1);

		//then
		assertThat(delivery.getDeliveryStatus()).isEqualTo(DeliveryStatus.READY);
		assertThat(delivery.getWaybillNo()).isEqualTo(waybillNo1);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void READY로_변경_실패_NULL_EMPTY(String waybillNo) {

		//then
		assertThrowsExactly(CustomException.class,
			() -> delivery.updateReady(waybillNo)
		);
	}

	@Test
	void 배송_상태_SHIPPING으로_변경() {

		delivery.updateReady(waybillNo1);
		delivery.updateShipping();

		assertThat(delivery.getDeliveryStatus()).isEqualTo(DeliveryStatus.SHIPPING);
		assertThat(delivery.getWaybillNo()).isEqualTo(waybillNo1);
	}

	@Test
	void 배송_상태_DELIVERED로_변경() {

		delivery.updateReady(waybillNo1);
		delivery.updateShipping();
		delivery.updateDelivered();

		assertThat(delivery.getDeliveryStatus()).isEqualTo(DeliveryStatus.DELIVERED);
		assertThat(delivery.getWaybillNo()).isEqualTo(waybillNo1);
	}

	@Test
	void 송장번호_변경() {

		delivery.updateReady(waybillNo1);

		delivery.updateReady(waybillNo2);

		assertThat(delivery.getDeliveryStatus()).isEqualTo(DeliveryStatus.READY);
		assertThat(delivery.getWaybillNo()).isEqualTo(waybillNo2);
	}
}