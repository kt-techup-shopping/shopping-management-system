package com.shop.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.domain.chat.Chat;
import com.shop.repository.chat.ChatRepository;
import com.shop.request.ChatRequest;
import com.shop.response.ChatResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

	private static final int PAGE_SIZE = 20;

	private final ChatRepository chatRepository;
	private final KafkaTemplate<String, ChatResponse> kafkaTemplate;
	private final String topicName = "chat-topic1";

	@Transactional
	public List<ChatResponse> getChats(
		Long roomId,
		LocalDateTime lastCreatedAt,
		UUID lastChatId
	) {

		Pageable pageable = PageRequest.of(0, PAGE_SIZE);

		List<Chat> chats;

		// 최초 진입
		if (lastCreatedAt == null || lastChatId == null) {
			chats = chatRepository
				.findTop20ByRoomIdOrderByCreatedAtDesc(roomId);
		}
		//  커서 기반 조회
		else {
			chats = chatRepository.findChatsByCursor(
				roomId,
				lastCreatedAt,
				lastChatId,
				pageable
			);
		}

		return chats.stream()
			.map(ChatResponse::from)
			.toList();
	}

	@Transactional
	public void sendMessage(String senderId, ChatRequest request) {
		// Entity → Response 변환
		ChatResponse response = new ChatResponse(UUID.randomUUID(), request.getRoomId(), senderId, request.getMessage(), LocalDateTime.now());

		// Kafka 발행
		kafkaTemplate.send(
			topicName,
			request.getRoomId().toString(),
			response
		);

		//  Kafka 발행
		kafkaTemplate.send(
			"chat-save",
			request.getRoomId().toString(),
			response
		);
	}

	@Transactional
	public void saveMessage(ChatResponse chatResponse){
		Chat chat  = new Chat(chatResponse.getChatId(), chatResponse.getSenderId(), chatResponse.getRoomId(), chatResponse.getMessage(), chatResponse.getCreatedAt());
		chatRepository.save(chat);
		log.info("chat 데이터 저장 완료: {}", chat);
	}
}
