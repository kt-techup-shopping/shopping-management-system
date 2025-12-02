package com.shop.global.security;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.shop.domain.user.model.Role;
import com.shop.domain.user.model.Status;
import com.shop.domain.user.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
	private static final String TOKEN_PREFIX = "Bearer ";
	private static final String BASE_LOGINID = "baseLoginId";

	private final JwtService jwtService;
	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String path = request.getRequestURI();
		if ("/auth/refresh".equals(path)) {
			filterChain.doFilter(request, response);
			return;
		}

		var header = request.getHeader(HttpHeaders.AUTHORIZATION);
		// Bearer {token}

		if (Strings.isBlank(header)) {
			filterChain.doFilter(request, response);
			return;
		}

		var token = header.substring(TOKEN_PREFIX.length());

		if (!jwtService.validate(token)) {
			filterChain.doFilter(request, response);
			return;
		}

		var id = jwtService.parseId(token);

		var user = userRepository
			.findByIdAndIsDeletedFalse(id)
			.orElse(null);

		if (user == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		if (user.getStatus() == Status.INACTIVE) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		var principal = new DefaultCurrentUser(id, user.getLoginId(), user.getRole());

		var techUpToken = new TechUpAuthenticationToken(
			principal,
			principal.getAuthorities()
		);

		SecurityContextHolder.getContext().setAuthentication(techUpToken);

		filterChain.doFilter(request, response);
	}
}
