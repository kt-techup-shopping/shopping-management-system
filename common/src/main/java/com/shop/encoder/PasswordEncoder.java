package com.shop.encoder;

public interface PasswordEncoder {
	String encode(CharSequence rawPassword);

	boolean matches(String rawPassword, String encodedPassword);
}