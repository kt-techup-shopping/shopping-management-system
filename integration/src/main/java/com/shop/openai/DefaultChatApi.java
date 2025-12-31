package com.shop.openai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DefaultChatApi implements OpenAiChatApi {
	private final ChatClient chatClient;

	@Override
	public String search(String query) {
		var response = chatClient.prompt()
			.user(query)
			.call().content();
		return response;
	}
}
