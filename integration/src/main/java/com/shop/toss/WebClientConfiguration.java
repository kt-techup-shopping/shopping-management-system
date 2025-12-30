package com.shop.toss;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebClientConfiguration {
	private final TossPaymentsProperties tossPaymentsProperties;

	@Bean
	public WebClient tossWebClient(WebClient.Builder builder) {
		return builder
			.baseUrl(tossPaymentsProperties.getBaseUrl())
			.build();
	}
}
