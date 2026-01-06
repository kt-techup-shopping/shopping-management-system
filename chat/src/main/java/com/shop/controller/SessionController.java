package com.shop.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.security.TechUpAuthenticationToken;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "세션", description = "세션 조회 API")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SessionController {

	private final RedisTemplate<String, Object> redisTemplate;

	@Operation(summary = "전체 세션 조회", description = "현재 사용중인 전체 세션을 조회하는 API")
	@GetMapping("/api/test/redis/sessions")
	public java.util.List<String> getWebsocketSessions() {
		// 일단 모든 키를 다 가져옴
		java.util.Set<String> allKeys = redisTemplate.keys("*");

		java.util.List<String> resultList = new java.util.ArrayList<>();

		if (allKeys == null || allKeys.isEmpty()) {
			resultList.add("📭 Redis가 비어있습니다.");
			return resultList;
		}

		resultList.add("총 키 개수: " + allKeys.size());
		resultList.add("필터링 조건: [String 타입] AND [WEBSOCKET_SESSION: 으로 시작]");
		resultList.add("==================================================");

		for (String key : allKeys) {
			// [필터 1] 키 이름이 "WEBSOCKET_SESSION:"으로 시작하는지 검사
			if (!key.startsWith("WEBSOCKET_SESSION:")) {
				continue; // 아니면 건너뜀
			}

			try {
				// [필터 2] Redis에 물어봐서 진짜 String 타입인지 검사 (에러 방지)
				org.springframework.data.redis.connection.DataType type = redisTemplate.type(key);

				if (org.springframework.data.redis.connection.DataType.STRING.equals(type)) {
					Object value = redisTemplate.opsForValue().get(key);

					String displayValue = "";

					// 인증 객체라면 보기 좋게 변환
					if (value instanceof TechUpAuthenticationToken) {
						TechUpAuthenticationToken auth = (TechUpAuthenticationToken) value;
						displayValue = "✅ [인증 유저] ID: " + auth.getCredentials();
					} else {
						displayValue = String.valueOf(value);
					}

					resultList.add("🔑 " + key);
					resultList.add("📦 " + displayValue);
					resultList.add("--------------------------------------------------");

				} else {
					// 이름은 맞는데 타입이 String이 아닌 경우 (혹시 모를 상황)
					resultList.add("⚠️ [타입 불일치] " + key + " (Type: " + type + ")");
				}

			} catch (Exception e) {
				resultList.add("❌ " + key + " 조회 중 에러: " + e.getMessage());
			}
		}

		if (resultList.size() <= 3) {
			resultList.add("🔍 조건에 맞는 세션 데이터가 없습니다.");
		}

		return resultList;
	}
}