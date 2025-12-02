package com.shop.domain.order.response;

import java.time.LocalDateTime;

import com.shop.domain.order.model.OrderStatus;
import com.shop.domain.payment.model.PaymentType;
import com.querydsl.core.annotations.QueryProjection;

public record OrderDetailUserQueryResponse(
	Long orderId,
	String receiverName,
	String receiverAddress,
	String receiverMobile,
	OrderStatus orderStatus,
	LocalDateTime deliveredAt,
	LocalDateTime orderedAt,

	Long productId,
	String productName,
	Long productPrice,
	Long quantity,

	Long paymentAmount,
	Long deliveryFee,
	PaymentType paymentType
) {

	@QueryProjection
	public OrderDetailUserQueryResponse {
	}
}
