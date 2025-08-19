package com.passtival.backend.global.auth.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.passtival.backend.global.auth.jwt.JwtUtil;
import com.passtival.backend.global.auth.model.CustomOAuth2User;
import com.passtival.backend.global.auth.model.token.TokenResponse;
import com.passtival.backend.global.common.BaseResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * OAuth2 로그인 성공 후 JWT 토큰 발급 및 응답 처리
 * LoginFilter의 successfulAuthentication 로직을 참고하여 구현
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private final JwtUtil jwtUtil;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {

		// 1. OAuth2 사용자 정보 추출 (LoginFilter의 CustomMemberDetails와 동일한 역할)
		CustomOAuth2User oauth2User = (CustomOAuth2User)authentication.getPrincipal();

		// 2. 사용자 ID와 역할 추출
		Long memberId = oauth2User.getMemberId(); // CustomOAuth2User에서 memberId 추출
		String role = extractRole(authentication.getAuthorities());

		// 3. JWT 토큰 생성 (LoginFilter와 동일한 로직)
		String accessToken = jwtUtil.createAccessToken(memberId, role);
		String refreshToken = jwtUtil.createRefreshToken(memberId, role);

		// 4. TokenResponse 생성 (LoginFilter와 동일한 구조)
		TokenResponse tokenResponse = TokenResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)  // OAuth2 로그인 시에도 두 토큰 모두 제공
			.build();

		BaseResponse<TokenResponse> successResponse = BaseResponse.success(tokenResponse);

		// 5. 응답 설정 (LoginFilter와 동일한 방식)
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		// 6. JSON 응답 전송 (LoginFilter와 동일한 방식)
		String jsonResponse = objectMapper.writeValueAsString(successResponse);
		response.getWriter().write(jsonResponse);
	}

	/**
	 * Role 추출 헬퍼 메서드 (LoginFilter와 동일한 로직)
	 */
	private String extractRole(Collection<? extends GrantedAuthority> authorities) {
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		return auth.getAuthority();
	}
}
