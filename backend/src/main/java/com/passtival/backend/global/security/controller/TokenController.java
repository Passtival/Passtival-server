package com.passtival.backend.global.security.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.global.security.model.token.RefreshTokenRequest;
import com.passtival.backend.global.security.model.token.TokenResponse;
import com.passtival.backend.global.security.service.TokenService;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.exception.BaseException;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

	@PostMapping("/refresh")
	public BaseResponse<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) throws
		BaseException {
		TokenResponse tokenResponse = tokenService.refreshAccessToken(request);
		return BaseResponse.success(tokenResponse);
	}
}