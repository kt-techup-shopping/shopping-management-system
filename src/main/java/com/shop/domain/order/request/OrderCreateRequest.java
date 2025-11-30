package com.shop.domain.order.request;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record OrderCreateRequest(
	@NotNull
	Map<Long, @Min(1) Long> productQuantity, // key: productId, value: quantity
	@NotBlank
	String receiverName,
	@NotBlank
	String receiverAddress,
	@NotBlank
	String receiverMobile
) {}
