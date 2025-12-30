package com.shop.openai.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenAIRequestVectorUploadFile(
	@JsonProperty("file_id")
	String id
) {
}
