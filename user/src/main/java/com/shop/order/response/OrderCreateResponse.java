package com.shop.order.response;

import com.shop.domain.order.Order;

public record OrderCreateResponse (
	Long orderId
){
	public static OrderCreateResponse from(Order order) {
		return new OrderCreateResponse(
			order.getId()
		);
	}
}
