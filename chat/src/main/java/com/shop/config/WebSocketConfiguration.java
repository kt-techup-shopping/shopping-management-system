package com.shop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

	private final StompHandler stompHandler; // 주입 받기

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws-chat").setAllowedOriginPatterns("*").withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// 서버에서 클라이언트로 보낼 때 사용하는 prefix
		registry.enableSimpleBroker("/sub");
		// 클라이언트에서 서버로 메시지를 보낼 때 사용하는 prefix
		registry.setApplicationDestinationPrefixes("/pub");
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		// stompHandler가 메시지보다 먼저 실행되도록 등록
		registration.interceptors(stompHandler);
	}
}