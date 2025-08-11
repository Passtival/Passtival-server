package com.passtival.backend.global.auth.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.passtival.backend.global.auth.security.CustomMemberDetails;
import com.passtival.backend.global.auth.jwt.JWTUtil;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        // 1. 클라이언트 요청에서 membername, password 추출
        String memberName = obtainUsername(request);
        String password = obtainPassword(request);

        log.info("로그인 시도: username = {}", memberName);

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
        CustomMemberDetails memberDetails = (CustomMemberDetails) authentication.getPrincipal();
        Long memberId = memberDetails.getMemberId(); // PhoneMatchUser의 userId
        String role = extractRole(authentication.getAuthorities());

        log.info("로그인 성공: memberId = {}, role = {}", memberId, role);

        // 2. JWT 토큰 생성
        String accessToken = jwtUtil.createAccessToken(memberId, "ROLE_" + role);
        String refreshToken = jwtUtil.createRefreshToken(memberId, "ROLE_" + role);

        // 3. JSON 응답 생성
        TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken);
        BaseResponse<TokenResponse> successResponse = BaseResponse.success(tokenResponse);

        // 4. 응답 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Authorization", "Bearer " + accessToken);

        // 5. JSON 응답 전송
        String jsonResponse = objectMapper.writeValueAsString(successResponse);
        response.getWriter().write(jsonResponse);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        log.warn("로그인 실패: {}", failed.getMessage());

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

    // 토큰 응답 DTO
    public static class TokenResponse {
        public String accessToken;
        public String refreshToken;

        public TokenResponse(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }
}