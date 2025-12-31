package com.shop.faq.service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.CustomException;
import com.shop.ErrorCode;
import com.shop.domain.faq.FAQ;
import com.shop.domain.vector.VectorType;
import com.shop.faq.request.FAQRequestCreate;
import com.shop.faq.request.FAQRequestSearch;
import com.shop.faq.response.FAQResponseSearch;
import com.shop.faq.response.FAQResponseSearchData;
import com.shop.openai.VectorApi;
import com.shop.openai.response.OpenAIResponseAttribute;
import com.shop.openai.response.OpenAIResponseContent;
import com.shop.openai.response.OpenAIResponseSearch;
import com.shop.repository.faq.FAQRepository;
import com.shop.repository.vector.VectorRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FAQService {
	private final FAQRepository faqRepository;
	private final VectorRepository vectorRepository;
	private final VectorApi vectorApi;
	private final ObjectMapper objectMapper;

	public void create(FAQRequestCreate request) throws Exception {
		var faq = faqRepository.save(
			new FAQ(
				request.title(),
				request.content(),
				request.category()
			)
		);

		var vectorData = Map.of(
			"id", faq.getId(),
			"title", faq.getTitle(),
			"content", faq.getContent(),
			"category", faq.getCategory(),

			// attributes를 별도 필드로 저장
			"attributes", Map.of(
				"author", faq.getCreatedBy(), // author 가 model에 하드코딩 되어있음
				"date", faq.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
		);

		var vector = vectorRepository.findByType(VectorType.FAQ)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_VECTOR_STORE));

		var fileId = vectorApi.uploadFile(vector.getStoredId(), objectMapper.writeValueAsBytes(vectorData));

		faq.updateFileId(fileId);
	}

	public void delete(Long id) {
		var faq = faqRepository.findById(id).orElseThrow(
			() -> new CustomException(ErrorCode.NOT_FOUND_FAQ)
		);
		var vector = vectorRepository.findByType(VectorType.FAQ)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_VECTOR_STORE));

		vectorApi.delete(vector.getStoredId(), faq.getFileId());

		faqRepository.delete(faq);
	}

	// 데이터 변환
	public FAQResponseSearch search(FAQRequestSearch request) {
		var vector = vectorRepository.findByType(VectorType.FAQ)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_VECTOR_STORE));

		// OpenAI API 호출
		var aiResponse = vectorApi.search(vector.getStoredId(), request.query());

		OpenAIResponseSearch searchResponse;
		try {
			searchResponse = objectMapper.convertValue(aiResponse, OpenAIResponseSearch.class);
		} catch (Exception e) {
			throw new CustomException(ErrorCode.ERROR_VECTOR_SEARCH);
		}

		List<FAQResponseSearchData> data = searchResponse.data().stream()
			.map(it -> {
				String textContent = it.content().stream()
					.filter(content -> "text".equals(content.type()))
					.map(OpenAIResponseContent::text)
					.findFirst()
					.orElse("");

				var attributes = extractAttributesFromData(textContent);

				var onlyContent = extractContentFromData(textContent);

				return FAQResponseSearchData.of(
					it.fileId(),
					it.filename(),
					it.score(),
					attributes,
					onlyContent
				);
			}).toList();

		return FAQResponseSearch.of(request.query(), data);
	}

	/**
	 * AI에게 도움을 받은 코드
	 * search를 할 때 attributes 부분이 null로 출력되어 attributes에 입력된 것을 출력하게 해줌
	 * 53번줄에 존재하는 "attributes", Map.of() 코드 포함
	 */

	// 메타데이터에서 author, date 추출
	private OpenAIResponseAttribute extractAttributesFromData(String rawContent) {
		try {
			JsonNode jsonNode = objectMapper.readTree(rawContent);
			JsonNode attributes = jsonNode.get("attributes");

			if (attributes != null) {
				String author = attributes.has("author") ? attributes.get("author").asText() : "null";
				String date = attributes.has("date") ? attributes.get("date").asText() : "Unknown Date";

				return new OpenAIResponseAttribute(author, date);
			}
		} catch (Exception e) {
			// 파싱 실패시 기본값
		}

		return new OpenAIResponseAttribute("null", "Unknown Date");
	}

	// FAQ 데이터만 추출
	private String extractContentFromData(String rawContent) {
		try {
			JsonNode jsonNode = objectMapper.readTree(rawContent);

			// metadata를 제외한 FAQ 데이터만 추출
			var faqOnly = Map.of(
				"id", jsonNode.get("id").asLong(),
				"title", jsonNode.get("title").asText(),
				"content", jsonNode.get("content").asText(),
				"category", jsonNode.get("category").asText()
			);

			return objectMapper.writeValueAsString(faqOnly);
		} catch (Exception e) {
			return rawContent; // 실패시 원본 반환
		}
	}
}