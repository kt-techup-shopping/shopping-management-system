package com.shop.faq.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FAQResponseSearch(
	String object,
	@JsonProperty("search_query")
	String searchQuery,
	List<FAQResponseSearchData> data,
	@JsonProperty("has_more")
	Boolean hasMore,
	@JsonProperty("next_page")
	Object nextPage
) {
	public static FAQResponseSearch of(String query, List<FAQResponseSearchData> data) {
		return new FAQResponseSearch(
			"vector_store.search_results.page",
			query,
			data,
			false,
			null
		);
	}
}
