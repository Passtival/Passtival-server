package com.passtival.backend.global.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.passtival.backend.global.auth.jwt.JWTUtil;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

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
        String token = authorizationHeader.split(" ")[1];

        try {
            // 4. 토큰 유효성 검증
            if (jwtUtil.validateToken(token)) {
                // 5. 사용자 정보 추출
                Long memberId = jwtUtil.getMemberId(token);
                String role = jwtUtil.getRole(token);

                if (memberId != null && role != null) {
                    // 6. Spring Security 인증 객체 생성
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    memberId, // principal (사용자 식별자)
                                    null,   // credentials (비밀번호 - JWT에서는 불필요)
                                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                            );

                    // 7. SecurityContext에 인증 정보 설정
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } else {
                // 토큰 무효 시 401 응답
                sendErrorResponse(response, BaseResponseStatus.TOKEN_INVALID);
                return;
            }

        } catch (Exception e) {
            log.warn("JWT 처리 중 오류 발생: {}", e.getMessage());
            sendErrorResponse(response, BaseResponseStatus.TOKEN_INVALID);
            return;
        }

        // 8. 다음 필터로 진행
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
