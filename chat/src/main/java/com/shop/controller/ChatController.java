package com.shop.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shop.ApiResult;
import com.shop.request.ChatRequest;
import com.shop.response.ChatResponse;
import com.shop.security.DefaultCurrentUser;
import com.shop.security.TechUpAuthenticationToken;
import com.shop.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller // WebSocket 메시지는 @Controller에서 처리
@RequiredArgsConstructor
public class ChatController {

	private final ChatService chatService;

	@MessageMapping("/chat/message")
	public void sendMessage(Principal principal, ChatRequest request) {

		// 형변환
		TechUpAuthenticationToken token = (TechUpAuthenticationToken) principal;

		// getPrincipal()이 DefaultCurrentUser를 반환하도록 만드셨으니 바로 꺼내서 사용
		DefaultCurrentUser user = (DefaultCurrentUser) token.getPrincipal();

		log.info("채팅 메시지 수신: {}", request);
		chatService.sendMessage(user.getLoginId(), request);
	}

	@ResponseBody
	@GetMapping("/chats/rooms/{roomId}")
	public ApiResult<List<ChatResponse>> getChats(
		@PathVariable Long roomId,
		@RequestParam(required = false) LocalDateTime lastCreatedAt,
		@RequestParam(required = false) UUID lastChatId
	) {
		return ApiResult.ok(chatService.getChats(roomId, lastCreatedAt, lastChatId));
	}

}