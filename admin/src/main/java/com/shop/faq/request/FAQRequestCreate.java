package com.shop.faq.request;

import com.shop.domain.faq.Category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FAQRequestCreate(
	@NotBlank
	String title,
	@NotBlank
	String content,
	@NotNull
	Category category
) {
}
