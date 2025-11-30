package com.shop.domain.order.response;

import java.time.LocalDateTime;
import java.util.List;

import com.shop.domain.order.model.OrderStatus;
import com.shop.domain.payment.model.PaymentType;

public record AdminOrderDetailUserResponse(
	Long orderId,
	Long userId,
	String userName,
	String userEmail,
	String userMobile,
	String receiverName,
	String receiverAddress,
	String receiverMobile,

	OrderStatus orderStatus,
	LocalDateTime deliveredAt,
	LocalDateTime orderedAt,

	List<OrderProductInfo> products,
	PaymentInfo paymentInfo
) {

	public record OrderProductInfo(
		Long productId,
		String name,
		Long price,
		Long quantity
	) {}

	public record PaymentInfo(
		Long paymentAmount,
		Long deliveryFee,
		PaymentType type
	) {}
}
