package com.shop.payment.request;


import com.shop.domain.payment.PaymentType;

import jakarta.validation.constraints.NotNull;

public record PaymentCreateRequest (
	@NotNull
	PaymentType type
) {
}
