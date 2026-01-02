package com.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.shop.encoder.PasswordEncoder;
import com.shop.security.JwtFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
	private final JwtFilter jwtFilter;

	private static final String[] GET_PERMIT_ALL = {
		"/index.html",
		"/js/**", "/css/**", "/images/**", "/favicon.ico",
		"/api/health/**", "/swagger-ui/**", "/v3/api-docs/**",
		"/products", "/products/*", "/cart/**",
		"/reviews", "/reviews/user", "/reviews/single",
		"/chats/rooms/**",
	};
	private static final String[] POST_PERMIT_ALL = {
		"/auth/login", "/auth/refresh",
		"/users/auth/signup",
		"/cart/**",
		"/products", "/products/*",
	};
	private static final String[] PUT_PERMIT_ALL = {
		"/api/v1/public/**",
	};
	private static final String[] PATCH_PERMIT_ALL = {"/api/v1/public/**"};
	private static final String[] DELETE_PERMIT_ALL = {"/api/v1/public/**"};

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new PasswordEncoder() {
			private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

			@Override
			public String encode(CharSequence rawPassword) {
				return bCryptPasswordEncoder.encode(rawPassword);
			}

			@Override
			public boolean matches(String rawPassword, String encodedPassword) {
				return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
			}
		};
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.sessionManagement(
				session ->
					session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.authorizeHttpRequests(
				request -> {
					request.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();
					request.requestMatchers(HttpMethod.GET, GET_PERMIT_ALL).permitAll();
					request.requestMatchers(HttpMethod.POST, POST_PERMIT_ALL).permitAll();
					request.requestMatchers(HttpMethod.PATCH, PATCH_PERMIT_ALL).permitAll();
					request.requestMatchers(HttpMethod.PUT, PUT_PERMIT_ALL).permitAll();
					// WebSocket (SockJS 포함)
                	request.requestMatchers("/ws-chat/**").permitAll();
					request.requestMatchers(HttpMethod.DELETE, DELETE_PERMIT_ALL).permitAll();

					request.anyRequest().authenticated();
				}
			)
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
			.csrf(AbstractHttpConfigurer::disable);

		return http.build();
	}
}