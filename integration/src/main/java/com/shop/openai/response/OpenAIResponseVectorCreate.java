package com.shop.openai.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenAIResponseVectorCreate(
	String id,
	String object,
	@JsonProperty("created_at")
	Long createdAt,
	String name,
	String description,
	Long bytes,
	@JsonProperty("file_counts")
	OpenAIResponseFileCounts fileCounts
) {
}
