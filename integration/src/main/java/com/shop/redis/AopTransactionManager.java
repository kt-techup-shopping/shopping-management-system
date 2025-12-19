package com.shop.redis;

import org.aspectj.lang.ProceedingJoinPoint;

public interface AopTransactionManager {
	Object proceed(ProceedingJoinPoint joinPoint) throws Throwable;
}
