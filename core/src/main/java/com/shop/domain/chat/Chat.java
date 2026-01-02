package com.shop.domain.chat;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Chat {

	// 채팅을 저장하는 시점이 아닌 보내는 시점에 id가 지정되어야 하므로
	@Id
	private UUID id;
	// MSA를 고려한 연관관계 설정 x
	// 연관관계 설정 시 유저와 연결되어야 합니다.
	private String senderId;
	// 연관관계 설정 시 product와 하거나, 안 해도 괜찮다고 생각합니다.
	private Long roomId;

	private LocalDateTime createdAt;

	@Column(nullable = false, length = 1000)
	private String message;

	public Chat(UUID chatId, String senderId, Long roomId, String message, LocalDateTime createdAt) {
		this.id = chatId;
		this.senderId = senderId;
		this.roomId = roomId;
		this.message = message;
		this.createdAt = createdAt;
	}

}
