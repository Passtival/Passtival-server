package com.passtival.backend.global.security.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.passtival.backend.global.security.model.CustomMemberDetails;
import com.passtival.backend.global.security.model.token.TokenResponse;
import com.passtival.backend.global.security.util.JwtUtil;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 커스텀 로그인 필터
 * 현재 소셜 로그인 전용 시스템으로 인해 사용되지 않음
 * 향후 확장성을 위해 보존됨
 */
@Deprecated //더 이상 사용하지 않는 코드를 표시
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
		throws AuthenticationException {
		// 1. 클라이언트 요청에서 membername, password 추출
		String memberName = obtainUsername(request);
		String password = obtainPassword(request);

		// 2. 인증 토큰 생성
		UsernamePasswordAuthenticationToken authToken =
			new UsernamePasswordAuthenticationToken(memberName, password, null);

		// 3. AuthenticationManager로 인증 시도
		return authenticationManager.authenticate(authToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		FilterChain chain, Authentication authentication) throws IOException {
		// 1. 사용자 정보 추출
		CustomMemberDetails memberDetails = (CustomMemberDetails)authentication.getPrincipal();
		Long memberId = memberDetails.getMemberId(); // PhoneMatchUser의 userId
		String role = extractRole(authentication.getAuthorities());

		// 2. JWT 토큰 생성
		String accessToken = jwtUtil.createAccessToken(memberId, role);
		String refreshToken = jwtUtil.createRefreshToken(memberId, role);

		// 3. TokenResponseDto 사용 (로그인 시에는 두 토큰 모두 포함)
		TokenResponse tokenResponse = TokenResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)  // 로그인 시에는 두 토큰 모두 제공
			.build();

		BaseResponse<TokenResponse> successResponse = BaseResponse.success(tokenResponse);

		// 4. 응답 설정
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		// 5. JSON 응답 전송
		String jsonResponse = objectMapper.writeValueAsString(successResponse);
		response.getWriter().write(jsonResponse);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException {

		// BaseResponse를 활용한 에러 응답
		response.setStatus(BaseResponseStatus.LOGIN_REQUIRED.getCode());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		BaseResponse<Object> errorResponse = BaseResponse.fail(BaseResponseStatus.LOGIN_REQUIRED);
		String jsonResponse = objectMapper.writeValueAsString(errorResponse);
		response.getWriter().write(jsonResponse);
	}

	// Role 추출 헬퍼 메서드
	private String extractRole(Collection<? extends GrantedAuthority> authorities) {
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		return auth.getAuthority();
	}

}