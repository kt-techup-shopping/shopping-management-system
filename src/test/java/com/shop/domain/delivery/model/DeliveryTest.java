package com.shop.domain.delivery.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.shop.domain.order.model.Order;

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

}