package com.shop.payment.vo;

import com.shop.CustomException;
import com.shop.ErrorCode;
import com.shop.Preconditions;

public record OrderId(
	Long orderId,
	Long paymentId
) {
	private static final String PREFIX = "ORD";

	public static String generate(Long orderId, Long paymentId) {
		return String.format(
			"%s_%06d_%06d",
			PREFIX,
			orderId,
			paymentId
		);
	}

	public static OrderId parse(String value) {
		try {
			var tokens = value.split("_");

			Preconditions.validate(tokens.length == 3, ErrorCode.INVALID_ORDER_ID);
			Preconditions.validate(PREFIX.equals(tokens[0]), ErrorCode.INVALID_ORDER_ID);

			Long orderId = Long.parseLong(tokens[1]);
			Long paymentId = Long.parseLong(tokens[2]);

			return new OrderId(orderId, paymentId);
		} catch (Exception e) {
			throw new CustomException(ErrorCode.INVALID_ORDER_ID);
		}
	}
}
