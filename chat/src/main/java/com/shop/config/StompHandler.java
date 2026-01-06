package com.shop.config;

import java.util.concurrent.TimeUnit;

import com.shop.CustomException;
import com.shop.ErrorCode;
import com.shop.domain.user.User;
import com.shop.jwt.JwtService;
import com.shop.repository.user.UserRepository;
import com.shop.security.DefaultCurrentUser;
import com.shop.security.TechUpAuthenticationToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99) // 우선순위 높게 설정
public class StompHandler implements ChannelInterceptor {

	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final RedisTemplate<String, Object> redisTemplate; // Redis 추가

	// Redis에 저장할 키 접두사
	private static final String SESSION_KEY_PREFIX = "WEBSOCKET_SESSION:";

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		StompCommand command = accessor.getCommand();
		String sessionId = accessor.getSessionId(); // 웹소켓 세션 ID

		// CONNECT: 인증 후 Redis에 저장
		if (StompCommand.CONNECT.equals(command)) {
			String token = extractToken(accessor);

			if (token != null && jwtService.validate(token)) {
				Long userId = jwtService.parseId(token);
				User user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

				// 인증 객체 생성
				var currentUser = new DefaultCurrentUser(userId, user.getLoginId(), user.getRole());
				TechUpAuthenticationToken auth = new TechUpAuthenticationToken(currentUser, currentUser.getAuthorities());

				// [Redis 저장] Key: 세션ID, Value: 인증객체, 유효시간: 24시간
				String redisKey = SESSION_KEY_PREFIX + sessionId;
				redisTemplate.opsForValue().set(redisKey, auth, 24, TimeUnit.HOURS);

				// 현재 요청에도 인증 정보 심어주기
				accessor.setUser(auth);

				log.info("CONNECT: Redis에 사용자 저장 완료 (Key= {} )", redisKey);

				// 재조립해서 리턴
				return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
			}
		}

		// SEND: Redis에서 꺼내와서 복구
		if (StompCommand.SEND.equals(command)) {
			String redisKey = SESSION_KEY_PREFIX + sessionId;

			// [Redis 조회]
			Object savedAuth = redisTemplate.opsForValue().get(redisKey);

			if (savedAuth == null) {
				log.info("SEND 차단: Redis에 인증 정보 없음");
				throw new CustomException(ErrorCode.NOT_FOUND_USER);
			}

			if (savedAuth instanceof TechUpAuthenticationToken) {
				TechUpAuthenticationToken auth = (TechUpAuthenticationToken) savedAuth;

				// 복구
				accessor.setUser(auth);

				log.info("SEND 허용: Redis에서 복구됨 (User= {} ", auth.getCredentials());

				// 재조립해서 리턴
				return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
			}
		}

		// DISCONNECT: (선택) 연결 끊기면 Redis에서 삭제
		if (StompCommand.DISCONNECT.equals(command)) {
			String redisKey = SESSION_KEY_PREFIX + sessionId;
			redisTemplate.delete(redisKey);
			log.info("DISCONNECT: Redis 데이터 삭제 완료(Key= {} )", redisKey);
		}

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