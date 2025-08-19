package com.passtival.backend.global.auth.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.passtival.backend.global.auth.jwt.JwtUtil;
import com.passtival.backend.global.auth.model.CustomMemberDetails;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;
	private final ObjectMapper objectMapper = new ObjectMapper();

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
			if (tokenInfo != null && !tokenInfo.isExpired()) {

				CustomMemberDetails memberDetails = new CustomMemberDetails(
					tokenInfo.memberId, tokenInfo.role);

				// SecurityContext에 인증 정보 설정
				UsernamePasswordAuthenticationToken authToken =
					new UsernamePasswordAuthenticationToken(
						memberDetails, null, memberDetails.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authToken);

			} else {
				// 토큰이 유효하지 않거나 만료된 경우
				sendErrorResponse(response, BaseResponseStatus.TOKEN_INVALID);
				return;
			}

		} catch (Exception e) {
			sendErrorResponse(response, BaseResponseStatus.TOKEN_INVALID);
			return;
		}

		// 다음 필터로 진행
		filterChain.doFilter(request, response);
	}

	/**
	 * 에러 응답 전송
	 */
	private void sendErrorResponse(HttpServletResponse response, BaseResponseStatus status)
		throws IOException {
		response.setStatus(status.getCode());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		BaseResponse<Object> errorResponse = BaseResponse.fail(status);
		String jsonResponse = objectMapper.writeValueAsString(errorResponse);

		response.getWriter().write(jsonResponse);
	}
}