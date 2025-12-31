package com.shop.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.vector.Vector;
import com.shop.domain.vector.VectorType;
import com.shop.openai.OpenAiChatApi;
import com.shop.repository.vector.VectorRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {
	private final OpenAiChatApi openAIChatApi;
	private final VectorRepository vectorRepository;

	public String questions(String query) {
		var ids = vectorRepository.findByTypeIn(VectorType.chatbotRange()).stream().map(Vector::getStoredId).toList();

		var newQuery = String.format("%s:%s", String.join(",", ids), query);

		return openAIChatApi.search(newQuery);
	}
}
