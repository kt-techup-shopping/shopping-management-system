package com.shop.openai.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenAIResponseUpload(
	String id,
	String object,
	Long bytes,
	@JsonProperty("created_at")
	Long createdAt,
	@JsonProperty("expires_at")
	Long expiresAt,
	String filename,
	String purpose
) {
}
