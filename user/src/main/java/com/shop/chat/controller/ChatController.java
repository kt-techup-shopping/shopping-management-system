package com.shop.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.shop.ApiResult;
import com.shop.chat.service.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "챗봇", description = "채팅봇 API")
@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {
	private final ChatService chatService;

	@Operation(summary = "질문하기", description = "챗봇 질문")
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<String> question(@RequestParam String query) {
		return ApiResult.ok(chatService.questions(query));
	}
}