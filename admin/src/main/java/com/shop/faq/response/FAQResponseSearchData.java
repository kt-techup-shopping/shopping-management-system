package com.shop.faq.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shop.openai.response.OpenAIResponseAttribute;

public record FAQResponseSearchData(
	@JsonProperty("file_id")
	String fileId,
	String filename,
	Double score,
	OpenAIResponseAttribute attributes,
	List<FAQResponseContent> content
) {
	public static FAQResponseSearchData of(String fileId, String filename, Double score,
		OpenAIResponseAttribute attributes, String textContent) {
		return new FAQResponseSearchData(
			fileId,
			filename,
			score,
			attributes,
			List.of(FAQResponseContent.text(textContent))
		);
	}
}