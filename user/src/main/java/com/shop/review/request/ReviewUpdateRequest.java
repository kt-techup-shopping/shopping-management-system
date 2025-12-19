package com.shop.review.request;

import jakarta.validation.constraints.NotBlank;

public record ReviewUpdateRequest(
	@NotBlank
	String title,
	@NotBlank
	String content
) {
}
