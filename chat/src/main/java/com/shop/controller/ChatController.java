package com.shop.controller;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.shop.request.ChatMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller // WebSocket 메시지는 @Controller에서 처리합니다.
public class ChatController {

	private final KafkaTemplate<String, ChatMessage> kafkaTemplate;
	private final String topicName = "chat-topic1"; // 채팅용 토픽

	public ChatController(KafkaTemplate<String, ChatMessage> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	// 클라이언트가 /pub/chat/message로 메시지를 보내면 호출됨
	@MessageMapping("/chat/message")
	public void sendMessage(ChatMessage message) {
		log.info("클라이언트로부터 메시지 수신: {}", message);
		// 수신된 메시지를 Kafka 토픽으로 발행
		kafkaTemplate.send(topicName, message.getRoomId(), message);
		log.info("클라이언트로부터 메시지 수신 완료: {}", message);
	}
}