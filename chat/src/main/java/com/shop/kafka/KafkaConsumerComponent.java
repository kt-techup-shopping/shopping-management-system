package com.shop.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import com.shop.response.ChatResponse;
import com.shop.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumerComponent {

	private static final String CHAT_SAVE_GROUP = "chat-save-group";

	private final SimpMessagingTemplate messagingTemplate;
	private final ChatService chatService;

	@KafkaListener(topics = "chat-topic1", groupId = "#{ 'chat-group-' + T(java.net.InetAddress).getLocalHost().getHostName() }")
	public void consume(ChatResponse message) {
		log.info("Kafka로부터 메시지 수신: {}", message);

		// WebSocket 구독자들에게 메시지 전송 (/sub/chat/room/{roomId})
		messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
	}

	// 단 하나의 서버에서만 처리하면 되는 명령어기 때문에 groupId 고정
	@KafkaListener(topics = "chat-save", groupId = CHAT_SAVE_GROUP)
	public void save(ChatResponse message){
		log.info("Kafka로부터 저장 요청 수신: {}", message);
		chatService.saveMessage(message);
	}
}
