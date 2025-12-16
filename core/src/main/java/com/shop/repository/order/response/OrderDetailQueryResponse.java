package com.shop.repository.order.response;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

public record OrderDetailQueryResponse(
	Long orderId,
	String receiverName,
	String receiverAddress,
	String receiverMobile,
	String productName,
	Long productPrice,
	Long quantity,
	String orderStatus,
	LocalDateTime deliveredAt
) {
	@QueryProjection
	public OrderDetailQueryResponse{};
}
