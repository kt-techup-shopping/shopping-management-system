package com.shop.review.request;

import jakarta.validation.constraints.NotBlank;

public record ReviewCreateRequest(
	@NotBlank
	String title,
	@NotBlank
	String content
) {
}
