package com.passtival.backend.global.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.passtival.backend.global.security.model.CustomMemberDetails;
import com.passtival.backend.global.security.util.JwtUtil;
import com.passtival.backend.global.security.util.ResponseUtil;
import com.passtival.backend.global.common.BaseResponseStatus;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;
	private final ResponseUtil responseUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// 1. Authorization 헤더 추출
		String authorizationHeader = request.getHeader("Authorization");

		// 2. Bearer 토큰 검증
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		// 3. 토큰 추출
		String token = authorizationHeader.substring(7); // "Bearer " 제거

		try {
			// 핵심 개선: 토큰 파싱을 한 번만 수행하여 모든 검증 완료
			JwtUtil.TokenInfo tokenInfo = jwtUtil.extractTokenInfo(token);

			// 파싱된 정보로 유효성 검증 (null 체크, 만료 확인, 필수 필드 모두 포함)

			CustomMemberDetails memberDetails = new CustomMemberDetails(
				tokenInfo.memberId, tokenInfo.role);

			// SecurityContext에 인증 정보 설정
			UsernamePasswordAuthenticationToken authToken =
				new UsernamePasswordAuthenticationToken(
					memberDetails, null, memberDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authToken);

		} catch (ExpiredJwtException e) {
			// 4. 토큰 만료 예외 처리
			responseUtil.sendErrorResponse(response, BaseResponseStatus.TOKEN_EXPIRED);
			return;
		} catch (JwtException e) {
			// 5. 서명 오류 또는 기타 JWT 관련 예외 처리
			log.warn("Invalid JWT token caught, token: {}", token, e);
			responseUtil.sendErrorResponse(response, BaseResponseStatus.TOKEN_INVALID);
			return;
		} catch (Exception e) {
			// 6. 그 외 예측하지 못한 모든 예외 처리
			log.error("Unexpected error occurred during JWT authentication, token: {}", token, e);
			responseUtil.sendErrorResponse(response, BaseResponseStatus.INTERNAL_SERVER_ERROR);
			return;
		}

		// 다음 필터로 진행
		filterChain.doFilter(request, response);
	}
}