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
	private static final double CONFIDENCE_THRESHOLD = 0.3;

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

		// 신뢰도 체크 및 시스템 메시지 설정
		String systemMessage;
		if (topScoreSearchData.score() < CONFIDENCE_THRESHOLD || topScoreSearchData.content() == null
			|| topScoreSearchData.content().toString().isEmpty()) {
			log.info("Low confidence score ({}) or empty content. Returning unknown information message.",
				topScoreSearchData.score());
			systemMessage = "사용자의 질문에 대한 정확한 정보를 찾을 수 없습니다. '사용자의 질문에 대한 정확한 정보를 찾을 수 없습니다.'라고 답변해주세요.";
		} else {
			systemMessage = String.format("다음 정보를 바탕으로 사용자의 질문에 답변해주세요: %s", topScoreSearchData.content().toString());
		}

		var newPrompt = prompt.augmentSystemMessage(systemMessage);

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