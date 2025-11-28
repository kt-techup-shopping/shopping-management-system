package com.shop.domain.payment.request;

import com.shop.domain.payment.model.PaymentType;

import jakarta.validation.constraints.NotNull;

public record PaymentCreateRequest (
	@NotNull
	PaymentType type
) {
}
