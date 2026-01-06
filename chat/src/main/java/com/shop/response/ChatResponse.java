package com.shop.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shop.domain.chat.Chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatResponse {

	private UUID chatId;        // cursor (lastChatId)
	private Long roomId;
	private String senderId;
	private String message;
	private LocalDateTime createdAt;

	public static ChatResponse from(Chat chat) {
		return new ChatResponse(
			chat.getId(),
			chat.getRoomId(),
			chat.getSenderId(),
			chat.getMessage(),
			chat.getCreatedAt()
		);
	}

}