package com.shop.service;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.CustomException;
import com.shop.ErrorCode;
import com.shop.Preconditions;
import com.shop.domain.user.Status;
import com.shop.encoder.PasswordEncoder;
import com.shop.jwt.JwtService;
import com.shop.repository.user.UserRepository;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final RefreshTokenService refreshTokenService;
	private final AccessTokenBlacklistService accessTokenBlacklistService;

	public Pair<String, String> login(String loginId, String password) {
		var user = userRepository.findByLoginId(loginId)
			.orElseThrow(() -> new CustomException(ErrorCode.FAIL_LOGIN));

		Preconditions.validate(passwordEncoder.matches(password, user.getPassword()), ErrorCode.FAIL_LOGIN);
		Preconditions.validate(user.getStatus() == Status.ACTIVE, ErrorCode.ACCOUNT_INACTIVATED);

		var accessExp = jwtService.getAccessExpiration();
		var refreshExp = jwtService.getRefreshExpiration();

		var accessToken = jwtService.issue(user.getId(), user.getRole(), accessExp);
		var refreshToken = jwtService.issue(user.getId(), user.getRole(), refreshExp);

		long refreshTtlMs = refreshExp.getTime() - System.currentTimeMillis();
		refreshTokenService.save(user.getId(), refreshToken, refreshTtlMs);

		return Pair.of(accessToken, refreshToken);
	}

	public Pair<String, String> refresh(String refreshToken) {
		Long userId;
		try {
			userId = jwtService.parseId(refreshToken);
		} catch (JwtException | IllegalArgumentException e) {
			throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
		}

		if (!refreshTokenService.isSame(userId, refreshToken)) {
			throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
		}

		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		var newAccessExp = jwtService.getAccessExpiration();
		var newRefreshExp = jwtService.getRefreshExpiration();

		var newAccessToken = jwtService.issue(user.getId(), user.getRole(), newAccessExp);
		var newRefreshToken = jwtService.issue(user.getId(), user.getRole(), newRefreshExp);

		long newRefreshTtlMs = newRefreshExp.getTime() - System.currentTimeMillis();
		refreshTokenService.save(user.getId(), newRefreshToken, newRefreshTtlMs);

		return Pair.of(newAccessToken, newRefreshToken);
	}

	public void logout(Long id, String accessToken) {
		refreshTokenService.delete(id);

		try {
			String jti = jwtService.parseJti(accessToken);
			var exp = jwtService.parseExpiration(accessToken);

			long ttlMs = exp.getTime() - System.currentTimeMillis();
			if (ttlMs > 0) {
				accessTokenBlacklistService.save(jti, ttlMs);
			}
		} catch (JwtException | IllegalArgumentException e) {
		}
	}
}
