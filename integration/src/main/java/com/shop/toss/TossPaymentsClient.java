package com.shop.toss;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.toss.response.TossPaymentsConfirmResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TossPaymentsClient {
	private final RestTemplate restTemplate = new RestTemplate();
	private final TossPaymentsProperties tossPaymentsProperties;
	private final ObjectMapper objectMapper;

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

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

		ResponseEntity<String> response = restTemplate.exchange(
			tossPaymentsProperties.getBaseUrl() + "/v1/payments/confirm",
			HttpMethod.POST,
			entity,
			String.class
		);

		try {
			return objectMapper.readValue(
				response.getBody(),
				TossPaymentsConfirmResponse.class
			);
		} catch (Exception e) {
			// TODO: 에러 처리 개선
			throw new RuntimeException("토스 결제 응답 파싱 실패", e);
		}
	}

}
