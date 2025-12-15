package com.shop.global.aspect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import com.shop.global.common.CustomException;
import com.shop.global.common.ErrorCode;
import com.shop.global.common.Lock;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class LockAspect {
	private final AopTransactionManager aopTransactionManager;
	private final RedissonClient redissonClient;

	@Around("@annotation(com.shop.global.common.Lock) && @annotation(lock)")
	public Object lock(ProceedingJoinPoint joinPoint, Lock lock) throws Throwable {
		var arguments = joinPoint.getArgs();
		var keyPrefix = lock.key().name().toLowerCase();

		// 락 키 목록을 저장할 리스트
		List<String> keys;

		// 인자가 리스트인지 확인
		if (lock.isList()) {
			// 인자가 List<Long>이라고 가정
			@SuppressWarnings("unchecked")
			// 정렬을 위해 불변객체를 가변객체로
			var identities = new ArrayList<> ((List<Long>) arguments[lock.index()]);

			// 데드락 방지를 위해 ID를 정렬
			Collections.sort(identities);

			// 각 ID에 대해 락 키 생성
			keys = identities.stream()
				.map(id -> String.format("%s:%d", keyPrefix, id))
				.collect(Collectors.toList());
		} else {
			// 기존 로직: 단일 Long 타입
			var identity = (Long) arguments[lock.index()];
			keys = List.of(String.format("%s:%d", keyPrefix, identity));
		}

		// 2. 락 객체 생성 (단일 락 또는 다중 락)
		RLock[] rLocks = keys.stream()
			.map(redissonClient::getLock)
			.toArray(RLock[]::new);

		RLock finalLock = rLocks.length == 1 ? rLocks[0] : redissonClient.getMultiLock(rLocks); //  다중 락 처리

		try {
			// 3. 락 획득 시도 (MultiLock은 내부적으로 모든 락에 대해 시도)
			var available = finalLock.tryLock(lock.waitTime(), lock.leaseTime(), lock.timeUnit());

			if (!available) {
				throw new CustomException(ErrorCode.FAIL_ACQUIRED_LOCK);
			}

			return aopTransactionManager.proceed(joinPoint);
		} finally {
			// 4. 락 해제
			// RLock, RMultiLock 모두 isHeldByCurrentThread와 unlock을 지원
			if (finalLock.isHeldByCurrentThread()) {
				finalLock.unlock();
			}
		}
	}
}