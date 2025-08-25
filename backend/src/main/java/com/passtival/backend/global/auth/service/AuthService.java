package com.passtival.backend.global.auth.service;

import org.springframework.stereotype.Service;

import com.passtival.backend.global.auth.jwt.JwtUtil;
import com.passtival.backend.global.auth.model.token.RefreshTokenRequest;
import com.passtival.backend.global.auth.model.token.TokenResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 토큰 관련 인증 서비스
 * 리프레시 토큰 검증 및 새로운 액세스 토큰 발급을 담당
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

	private final JwtUtil jwtUtil;

	/**
	 * 리프레시 토큰을 통한 새로운 액세스 토큰 발급
	 * @param request 리프레시 토큰이 포함된 요청 객체
	 * @return TokenResponse 새로운 액세스 토큰
	 * @throws BaseException 토큰이 유효하지 않거나 처리 중 오류 발생 시
	 */
	public TokenResponse refreshAccessToken(RefreshTokenRequest request) throws BaseException {
		try {

			// 1. 수동 검증: 리프레시 토큰 null/empty 체크
			validateRefreshTokenRequest(request);

			String refreshToken = request.getRefreshToken().trim();

			// 2. 토큰 파싱 및 검증
			JwtUtil.TokenInfo tokenInfo = parseAndValidateToken(refreshToken);

			// 3. 새로운 액세스 토큰 생성
			String newAccessToken = jwtUtil.createAccessToken(tokenInfo.memberId, tokenInfo.role);

			// 4. 응답 생성 (refreshToken은 포함하지 않음)
			return TokenResponse.builder()
				.accessToken(newAccessToken)
				.build();

		} catch (BaseException e) {
			throw e;
		} catch (Exception e) {
			throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 리프레시 토큰 요청 검증
	 * @param request 리프레시 토큰 요청
	 * @throws BaseException 요청이 유효하지 않은 경우
	 */
	private void validateRefreshTokenRequest(RefreshTokenRequest request) throws BaseException {
		try {
			if (request == null) {
				throw new BaseException(BaseResponseStatus.BAD_REQUEST);
			}

			if (request.getRefreshToken() == null || request.getRefreshToken().trim().isEmpty()) {
				throw new BaseException(BaseResponseStatus.REFRESH_TOKEN_REQUIRED);
			}

		} catch (BaseException e) {
			throw e;
		} catch (Exception e) {
			throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 토큰 파싱 및 검증
	 * @param refreshToken 리프레시 토큰
	 * @return TokenInfo 파싱된 토큰 정보
	 * @throws BaseException 토큰이 유효하지 않은 경우
	 */
	private JwtUtil.TokenInfo parseAndValidateToken(String refreshToken) throws BaseException {
		try {
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

		} catch (BaseException e) {
			throw e; // BaseException은 그대로 전파
		} catch (Exception e) {
			throw new BaseException(BaseResponseStatus.TOKEN_INVALID);
		}
	}
}