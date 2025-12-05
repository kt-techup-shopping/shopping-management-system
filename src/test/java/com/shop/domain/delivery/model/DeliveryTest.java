package com.shop.domain.delivery.model;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import com.shop.domain.order.model.Order;
import com.shop.global.common.CustomException;

class DeliveryTest {

	@Test
	void 배송_생성_성공() {
		var order = new Order();
		var delivery = new Delivery(order);

		assertThat(delivery.getOrder()).isEqualTo(order);
		assertThat(delivery.getDeliveryStatus()).isEqualTo(DeliveryStatus.PENDING);
		assertThat(delivery.getWaybillNo()).isNull();
	}

	@Test
	void 배송_상태_PENDING으로_변경() {
		var order = new Order();
		var delivery = new Delivery(order);
		delivery.updateReady("CJ-123-456-7890"); // 먼저 READY 상태로 변경

		delivery.updatePending();

		assertThat(delivery.getDeliveryStatus()).isEqualTo(DeliveryStatus.PENDING);
		assertThat(delivery.getWaybillNo()).isNull();
	}

	@Test
	void 배송_상태_READY로_변경() {
		var order = new Order();
		var delivery = new Delivery(order);

		delivery.updateReady("CJ-123-456-7890");

		assertThat(delivery.getDeliveryStatus()).isEqualTo(DeliveryStatus.READY);
		assertThat(delivery.getWaybillNo()).isEqualTo("CJ-123-456-7890");
	}

	@ParameterizedTest
	@NullAndEmptySource
	void READY로_변경_실패_NULL_EMPTY(String waybillNo) {
		var order = new Order();
		var delivery = new Delivery(order);

		assertThrowsExactly(CustomException.class,
			() -> delivery.updateReady(waybillNo)
		);
	}

	@Test
	void 배송_상태_SHIPPING으로_변경() {
		var order = new Order();
		var delivery = new Delivery(order);

		delivery.updateReady("CJ-123-456-7890");
		delivery.updateShipping();

		assertThat(delivery.getDeliveryStatus()).isEqualTo(DeliveryStatus.SHIPPING);
		assertThat(delivery.getWaybillNo()).isEqualTo("CJ-123-456-7890");
	}
}