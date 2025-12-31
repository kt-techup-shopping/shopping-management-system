package com.shop.openai.response;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenAIResponseSearchData(
	@JsonProperty("file_id")
	String fileId,
	String filename,
	Double score,
	OpenAIResponseAttribute attributes,
	List<OpenAIResponseContent> content
) {
	public OpenAIResponseSearchData {
		if (content == null) {
			content = Collections.emptyList();
		}
	}
}
