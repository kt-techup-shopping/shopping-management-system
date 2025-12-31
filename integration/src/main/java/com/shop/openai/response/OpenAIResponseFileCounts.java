package com.shop.openai.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenAIResponseFileCounts(
	@JsonProperty("in_progress")
	int inProgress,
	int completed,
	int failed,
	int cancelled,
	int total
) {
}
