package com.passtival.backend.global.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.passtival.backend.global.security.util.ResponseUtil;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

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

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException, ServletException {

		// 1. CustomMemberDetailsService에서 던진 BaseException 처리
		if (exception.getCause() instanceof BaseException) {
			BaseException baseException = (BaseException)exception.getCause();
			BaseResponseStatus status = baseException.getStatus();
			responseUtil.sendErrorResponse(response, status);
			return;
		}

		// 2. CustomOAuth2UserService에서 던진 OAuth2AuthenticationException 처리
		if (exception instanceof OAuth2AuthenticationException) {
			responseUtil.sendErrorResponse(response, BaseResponseStatus.OAUTH2_PROCESSING_ERROR);
			return;
		}

		//아래 오류가 있어야 지 처리하지 못한 어떤 오류인지 종류를 알 수 있음
		log.error("예측하지 못한 인증 오류 발생", exception);
		responseUtil.sendErrorResponse(response, BaseResponseStatus.INTERNAL_SERVER_ERROR);
	}
}