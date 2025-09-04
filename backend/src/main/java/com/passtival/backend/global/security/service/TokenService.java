package com.passtival.backend.global.security.service;

import org.springframework.stereotype.Service;

import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;
import com.passtival.backend.global.security.model.token.TokenResponse;
import com.passtival.backend.global.security.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 토큰 관련 인증 서비스
 * 리프레시 토큰 검증 및 새로운 액세스 토큰 발급을 담당
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

	private final JwtUtil jwtUtil;

	/**
	 * 리프레시 토큰을 통한 새로운 액세스 토큰 발급
	 * @param refreshToken 순수한 리프레시 토큰 (Bearer 접두사 제거된 상태)
	 * @return TokenResponse 새로운 액세스 토큰
	 */
	public TokenResponse refreshAccessToken(String refreshToken) {

		// 1. 수동 검증: 리프레시 토큰 null 체크
		validateRefreshTokenRequest(refreshToken);

		// 2. 토큰 파싱 및 검증
		JwtUtil.TokenInfo tokenInfo = parseAndValidateToken(refreshToken);

		// 3. 새로운 액세스 토큰 생성
		String newAccessToken = jwtUtil.createAccessToken(tokenInfo.memberId, tokenInfo.role);

		// 4. 응답 생성 (refreshToken은 포함하지 않음)
		return TokenResponse.builder()
			.accessToken(newAccessToken)
			.build();

	}

	/**
	 * 리프레시 토큰 요청 검증
	 * @param token 리프레시 토큰 요청
	 */
	private void validateRefreshTokenRequest(String token) {
		if (token == null) {
			throw new BaseException(BaseResponseStatus.BAD_REQUEST);
		}
	}

	/**
	 * 토큰 파싱 및 검증
	 * @param refreshToken 리프레시 토큰
	 * @return TokenInfo 파싱된 토큰 정보
	 */
	private JwtUtil.TokenInfo parseAndValidateToken(String refreshToken) {
		JwtUtil.TokenInfo tokenInfo = jwtUtil.extractTokenInfo(refreshToken);

		// 토큰 파싱 실패 또는 정보 부족 체크
		if (tokenInfo == null || tokenInfo.memberId == null || tokenInfo.role == null) {
			throw new BaseException(BaseResponseStatus.TOKEN_INVALID);
		}

		// 토큰 만료 여부 검증
		if (tokenInfo.isExpired()) {
			throw new BaseException(BaseResponseStatus.TOKEN_EXPIRED);
		}

		return tokenInfo;

	}
}