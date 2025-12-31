package com.shop.openai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.ai.openai")
public record OpenAIProperties(
	String apiKey
) {

}