package com.shop.openai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class OpenAIConfiguration {
	@Bean
	public ChatClient chatClient(ChatClient.Builder builder, BaseAdvisor openAICustomAdvisor) {
		return builder
			.defaultAdvisors(openAICustomAdvisor)
			.build();
	}
}
