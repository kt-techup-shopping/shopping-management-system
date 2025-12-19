package com.shop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Lock {
	Key key();

	int index() default 0;

	boolean isList() default false;

	long waitTime() default 2L;

	long leaseTime() default 1L;

	TimeUnit timeUnit() default TimeUnit.SECONDS;

	enum Key {
		PRODUCT,
		STOCK,
		ORDER, USER_EVENT
	}
}
