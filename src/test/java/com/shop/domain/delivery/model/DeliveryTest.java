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

}