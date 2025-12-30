package com.shop.config;

import com.shop.CustomException;
import com.shop.ErrorCode;
import com.shop.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99) // 우선순위 높게 설정
public class StompHandler implements ChannelInterceptor {

	private final JwtService jwtService;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		// 클라이언트가 보낸 명령어 확인
		StompCommand command = accessor.getCommand();

		// 1. SEND (메시지 전송) 요청일 때만 토큰 검사
		if (StompCommand.SEND.equals(command)) {
			String token = extractToken(accessor);

			if (token == null || !jwtService.validate(token)) {
				log.error("유효하지 않은 토큰으로 메시지 전송 시도");
				throw new CustomException(ErrorCode.NOT_FOUND_USER);
			}
		}

		// 2. CONNECT, SUBSCRIBE 등은 검사 안 함 (그냥 통과)
		return message;
	}

	// 헤더에서 토큰 꺼내기
	private String extractToken(StompHeaderAccessor accessor) {
		String bearerToken = accessor.getFirstNativeHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}