package com.shop.domain.review.request;

import jakarta.validation.constraints.NotBlank;

public record ReviewUpdateRequest(
	@NotBlank
	String title,
	@NotBlank
	String content
) {
}
