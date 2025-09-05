package com.passtival.backend.global.handler;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.security.model.CustomOAuth2User;
import com.passtival.backend.global.security.util.JwtUtil;
import com.passtival.backend.global.security.util.ResponseUtil;

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

	private final ResponseUtil responseUtil;
	private final JwtUtil jwtUtil;

	@Value("${frontend.login-url}")
	private String longinUrl;

	@Value("${frontend.login-fail-url}")
	private String longinFailUrl;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {
		try {
			// 1. OAuth2 사용자 정보 추출 (LoginFilter의 CustomMemberDetails와 동일한 역할)
			CustomOAuth2User oauth2User = (CustomOAuth2User)authentication.getPrincipal();
			if (oauth2User == null) {
				responseUtil.sendErrorResponse(response, BaseResponseStatus.INTERNAL_SERVER_ERROR);
				return;
			}

			// 2. 사용자 ID와 역할 추출
			Long memberId = oauth2User.getMemberId(); // CustomOAuth2User에서 memberId 추출
			String role = extractRole(authentication.getAuthorities());

			//실패시 프론트엔드의 로그인 실패창으로 이동
			if (memberId == null || role == null) {
				response.sendRedirect(longinFailUrl);
				return;
			}

			// 3. JWT 토큰 생성 (LoginFilter와 동일한 로직)
			String accessToken = jwtUtil.createAccessToken(memberId, role);
			String refreshToken = jwtUtil.createRefreshToken(memberId, role);

			//성공시 프론트엔드의 로그인 후 창으로 이동
			String redirectUrl = String.format("%s?access_token=%s&refresh_token=%s",
				longinUrl, accessToken, refreshToken);

			response.sendRedirect(redirectUrl);

		} catch (Exception e) {
			// 어떤 사용자의 로그인 과정에서, 어떤 예외가 발생했는지 로그로 남겨야 디버깅이 가능합니다.
			//실패시 프론트엔드의 로그인 실패창으로 이동
			log.error("OAuth2 Success Handling 중 예외 발생, Principal: {}", authentication.getName(), e);
			response.sendRedirect(longinFailUrl);
		}
	}

	/**
	 * Role 추출 헬퍼 메서드 (LoginFilter와 동일한 로직)
	 */
	private String extractRole(Collection<? extends GrantedAuthority> authorities) {
		if (authorities == null || authorities.isEmpty()) {
			return null; // 상위에서 null 체크로 처리
		}

		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();

		return auth.getAuthority();
	}
}
