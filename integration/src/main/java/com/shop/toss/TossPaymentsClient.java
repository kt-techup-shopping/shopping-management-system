package com.shop.toss;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.toss.response.TossPaymentsConfirmResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TossPaymentsClient {
	private final WebClient tossWebClient;
	private final TossPaymentsProperties tossPaymentsProperties;

	public TossPaymentsConfirmResponse confirm(String paymentKey, String orderId, Long amount) {
		String basicAuth = Base64
			.getEncoder()
			.encodeToString((tossPaymentsProperties.getSecretKey() + ":").getBytes(StandardCharsets.UTF_8));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic " + basicAuth);

		Map<String, Object> body = Map.of(
			"paymentKey", paymentKey,
			"orderId", orderId,
			"amount", amount
		);

		return tossWebClient
			.post()
			.uri("/v1/payments/confirm")
			.contentType(MediaType.APPLICATION_JSON)
			.header(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth)
			.bodyValue(body)
			.retrieve()
			.bodyToMono(TossPaymentsConfirmResponse.class)
			.block();
	}

}
