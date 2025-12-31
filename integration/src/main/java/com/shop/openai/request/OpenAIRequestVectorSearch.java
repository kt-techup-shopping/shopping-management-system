package com.shop.openai.request;

public record OpenAIRequestVectorSearch(
	/**
	 * query = 검색할 질문
	 * topK = 가져올 문서 개수
	 * threshold = 유사도 임계값
	 */
	String query
) {
}
