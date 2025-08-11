package com.passtival.backend.global.auth.service;


import com.passtival.backend.global.auth.dto.RefreshTokenRequest;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.auth.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * JWT 토큰 관련 인증 서비스
 * 리프레시 토큰 검증 및 새로운 액세스 토큰 발급을 담당
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JWTUtil jwtUtil;

    /**
     * 리프레시 토큰을 통한 새로운 액세스 토큰 발급
     *
     * @param request 리프레시 토큰이 포함된 요청 객체
     * @return BaseResponse<TokenResponse> 새로운 액세스 토큰 또는 에러 응답
     *
     * 비즈니스 로직:
     * 1. 리프레시 토큰 유효성 검증 (형식, 서명, 만료시간)
     * 2. 토큰에서 사용자 정보 추출 (userId, role)
     * 3. 새로운 액세스 토큰 생성
     * 4. 응답 반환
     */
    public BaseResponse<TokenResponse> refreshAccessToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // 1. 리프레시 토큰 유효성 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            log.warn("유효하지 않은 리프레시 토큰: {}", refreshToken.substring(0, 20) + "...");
            return BaseResponse.fail(BaseResponseStatus.TOKEN_INVALID, "리프레시 토큰이 유효하지 않습니다.");
        }

        // 2. 리프레시 토큰 만료 확인
        if (jwtUtil.isExpired(refreshToken)) {
            log.warn("만료된 리프레시 토큰");
            return BaseResponse.fail(BaseResponseStatus.TOKEN_EXPIRED, "리프레시 토큰이 만료되었습니다.");
        }

        try {
            // 3. 토큰에서 사용자 정보 추출
            Long memberId = jwtUtil.getMemberId(refreshToken);
            String role = jwtUtil.getRole(refreshToken);

            if (memberId == null || role == null) {
                log.warn("리프레시 토큰에서 사용자 정보 추출 실패");
                return BaseResponse.fail(BaseResponseStatus.TOKEN_INVALID, "토큰에서 사용자 정보를 찾을 수 없습니다.");
            }

            // 4. 새로운 액세스 토큰 생성
            String newAccessToken = jwtUtil.createAccessToken(memberId, role);

            log.info("액세스 토큰 갱신 성공: memberId = {}, role = {}", memberId, role);

            // 5. 응답 생성
            TokenResponse tokenResponse = new TokenResponse(newAccessToken);
            return BaseResponse.success(tokenResponse);

        } catch (Exception e) {
            log.error("토큰 갱신 중 예외 발생: {}", e.getMessage());
            return BaseResponse.fail(BaseResponseStatus.INTERNAL_SERVER_ERROR, "토큰 갱신 중 오류가 발생했습니다.");
        }
    }

    /**
     * 토큰 응답 DTO
     * 새로운 액세스 토큰만 반환 (리프레시 토큰은 재사용)
     */
    public static class TokenResponse {
        public String accessToken;

        public TokenResponse(String accessToken) {
            this.accessToken = accessToken;
        }
    }
}