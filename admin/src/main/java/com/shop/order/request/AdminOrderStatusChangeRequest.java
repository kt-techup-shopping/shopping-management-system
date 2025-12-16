package com.shop.order.request;

import com.shop.domain.order.OrderStatus;

import jakarta.validation.constraints.NotNull;

public record AdminOrderStatusChangeRequest(
	@NotNull
	OrderStatus orderStatus
) {
}
