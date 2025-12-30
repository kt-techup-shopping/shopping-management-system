package com.shop.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.shop.request.ChatMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumerComponent {

	private final SimpMessagingTemplate messagingTemplate;

	@KafkaListener(topics = "chat-topic1", groupId = "#{ 'chat-group-' + T(java.net.InetAddress).getLocalHost().getHostName() }")
	public void consume(ChatMessage message) {
		log.info("Kafka로부터 메시지 수신: {}", message);

		// WebSocket 구독자들에게 메시지 전송 (/sub/chat/room/{roomId})
		messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
	}

}
