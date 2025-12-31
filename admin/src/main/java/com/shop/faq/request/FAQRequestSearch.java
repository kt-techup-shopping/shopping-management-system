package com.shop.faq.request;

import jakarta.validation.constraints.NotBlank;

public record FAQRequestSearch(
	@NotBlank
	String query
) {
}
