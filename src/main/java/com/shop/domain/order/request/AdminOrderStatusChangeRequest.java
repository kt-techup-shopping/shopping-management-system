package com.shop.domain.order.request;

import com.shop.domain.order.model.OrderStatus;

import jakarta.validation.constraints.NotNull;

public record AdminOrderStatusChangeRequest(
	@NotNull
	OrderStatus orderStatus
) {
}
