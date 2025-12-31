package com.shop.openai.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenAIResponseSearch(
	String object,
	@JsonProperty("search_query")
	List<String> searchQuery,
	List<OpenAIResponseSearchData> data,
	@JsonProperty("has_more")
	Boolean hasMore,
	@JsonProperty("next_page")
	Object nextPage
) {
}
