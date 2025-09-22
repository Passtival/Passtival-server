package com.passtival.backend.global.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;
import com.passtival.backend.global.security.util.ResponseUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private final ResponseUtil responseUtil;

	@Value("${frontend.login-fail-url}")
	private String longinFailUrl;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException, ServletException {

		// 사용자가 OAuth 로그인을 취소한 경우 처리
		String error = request.getParameter("error");
		if ("access_denied".equals(error)) {
			// 프론트엔드 로그인 페이지로 리다이렉트
			response.sendRedirect(longinFailUrl);
			return;
		}

		// 1. CustomMemberDetailsService에서 던진 BaseException 처리
		if (exception.getCause() instanceof BaseException) {
			BaseException baseException = (BaseException)exception.getCause();
			BaseResponseStatus status = baseException.getStatus();
			responseUtil.sendErrorResponse(response, status);
			return;
		}

		// 2. CustomOAuth2UserService에서 던진 OAuth2AuthenticationException 처리
		if (exception instanceof OAuth2AuthenticationException) {
			// OAuth 에러 코드 확인
			OAuth2AuthenticationException oauth2Exception = (OAuth2AuthenticationException)exception;
			String errorCode = oauth2Exception.getError().getErrorCode();

			if ("access_denied".equals(errorCode)) {
				response.sendRedirect(longinFailUrl);
				return;
			}

			if ("authorization_request_not_found".equals(errorCode)) {
				log.error("OAuth2 인증 오류: authorization_request_not_found");
			}

			if (!"authorization_request_not_found".equals(errorCode)) {
				log.error("OAuth2 인증 오류: {}", errorCode, exception);
			}

			responseUtil.sendErrorResponse(response, BaseResponseStatus.OAUTH2_PROCESSING_ERROR);
			return;
		}

		//아래 오류가 있어야 지 처리하지 못한 어떤 오류인지 종류를 알 수 있음
		log.error("예측하지 못한 인증 오류 발생", exception);
		responseUtil.sendErrorResponse(response, BaseResponseStatus.INTERNAL_SERVER_ERROR);
	}
}