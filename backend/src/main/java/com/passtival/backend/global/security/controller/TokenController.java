package com.passtival.backend.global.security.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;
import com.passtival.backend.global.security.model.token.TokenResponse;
import com.passtival.backend.global.security.service.TokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 인증 관련 API를 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증/인가 관련 API", description = "토큰 갱신")
public class TokenController {

	private final TokenService tokenService;

	@Operation(
		summary = "액세스 토큰 갱신",
		description = "리프레시 토큰을 Authorization 헤더에 담아서 새로운 액세스 토큰을 발급받습니다."
	)
	@PostMapping("/refresh")
	public BaseResponse<TokenResponse> refreshToken(
		@Parameter(
			description = "Bearer {refreshToken} 형식의 리프레시 토큰",
			example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
			required = true
		)
		@RequestHeader("Authorization") String headerToken) {

		if (headerToken == null || !headerToken.startsWith("Bearer ")) {
			throw new BaseException(BaseResponseStatus.INVALID_TOKEN_FORMAT);
		}

		// 토큰 추출 (Bearer 제거)
		String refreshToken = headerToken.substring(7).trim();

		TokenResponse tokenResponse = tokenService.refreshAccessToken(refreshToken);

		return BaseResponse.success(tokenResponse);
	}
}