package com.shop.openai;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.shop.profile.AppProfile;
import com.shop.profile.DevProfile;
import com.shop.profile.LocalProfile;

@Configuration
@DevProfile
@AppProfile
@LocalProfile
public class OpenAIClientConfig {

	@Bean
	public OpenAIClient openAIClient(RestClient.Builder builder) {
		var restClient = builder
			.baseUrl("https://api.openai.com/v1")
			.build();

		// RestClient를 HttpServiceProxyFactory가 이해할 수 있는 어댑터로 변환
		var adapter = RestClientAdapter.create(restClient);
		// HttpServiceProxyFactory 및 클라이언트 생성
		var factory = HttpServiceProxyFactory.builderFor(adapter).build();

		return factory.createClient(OpenAIClient.class);
	}
}