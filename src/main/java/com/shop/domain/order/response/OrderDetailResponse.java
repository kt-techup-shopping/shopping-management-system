package com.shop.domain.order.response;

import java.time.LocalDateTime;
import java.util.List;

import com.shop.domain.order.model.OrderStatus;

public record OrderDetailResponse(
	Long orderId,
	String receiverName,
	String receiverAddress,
	String receiverMobile,
	String orderStatus,
	LocalDateTime deliveredAt,
	List<OrderProductInfo> products
) {
	public record OrderProductInfo(
		String productName,
		Long price,
		Long quantity
	) {}
}
