package com.shop.openai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.stereotype.Component;

import com.shop.openai.request.OpenAIRequestVectorSearch;
import com.shop.openai.response.OpenAIResponseSearchData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAICustomAdvisor implements BaseAdvisor {
	private final OpenAIClient openAIClient;
	private final OpenAIProperties openAIProperties;

	@NotNull
	@Override
	public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
		var prompt = chatClientRequest.prompt();
		var message = prompt.getUserMessage().getText();
		log.info("Advisor Input Message: {}", message);

		var candidateAnswers = new ArrayList<OpenAIResponseSearchData>();

		var parsing = message.split(":");
		// 입력 포맷 검증 로그
		if (parsing.length < 2) {
			log.warn("Invalid message format. Expected 'id:query', but got: {}", message);
		}

		// 검색어 앞뒤 공백 제거
		var request = new OpenAIRequestVectorSearch(parsing[1].trim());

		var ids = parsing[0].split(",");

		Arrays.stream(ids).forEach(id -> {
			// 수정: parsing[0] 대신 개별 id 사용 및 로그 추가
			log.info("Searching Vector Store ID: {}", id);
			var response = openAIClient.search(id, String.format("Bearer %s", openAIProperties.apiKey()), request);

			var searchData = response.data().stream().max(Comparator.comparingDouble(OpenAIResponseSearchData::score)).orElse(
				new OpenAIResponseSearchData("", "", 0.0, null, null)
			);
			log.info("Search Result for ID {}: Score={}, Data={}", id, searchData.score(), searchData);

			if (searchData.content().isEmpty()) {
				log.warn("Warning: Search data contents are empty! The chatbot will not receive any context.");
			}

			candidateAnswers.add(searchData);
		});

		var topScoreSearchData = candidateAnswers.stream()
			.max(Comparator.comparingDouble(OpenAIResponseSearchData::score))
			.orElse(
				new OpenAIResponseSearchData("", "", 0.0, null, null)
			);

		log.info("Best Match File ID: {}, Score: {}", topScoreSearchData.fileId(), topScoreSearchData.score());
		var newPrompt = prompt.augmentSystemMessage(topScoreSearchData.content().toString());

		return chatClientRequest.mutate()
			.prompt(newPrompt)
			.build();
	}

	@Override
	public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
		return chatClientResponse;
	}

	@Override
	public int getOrder() {
		return 0;
	}
}
