package com.passtival.backend.global.auth.controller;

import com.passtival.backend.global.auth.dto.RefreshTokenRequest;
import com.passtival.backend.global.auth.service.AuthService;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 API를 처리하는 컨트롤러
 * JWT 토큰 갱신 등의 인증 관련 엔드포인트를 제공
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 리프레시 토큰을 통한 액세스 토큰 갱신 API
     * POST /api/auth/refresh
     *
     * @param request 리프레시 토큰이 포함된 요청 객체
     * @return BaseResponse<AuthService.TokenResponse> 새로운 액세스 토큰 또는 에러 응답
     *
     * 사용 시나리오:
     * 1. 클라이언트의 액세스 토큰이 만료됨 (15분 후)
     * 2. 저장된 리프레시 토큰을 이용해 이 API 호출
     * 3. 새로운 액세스 토큰을 받아서 계속 사용
     * 4. 리프레시 토큰도 만료되면 다시 로그인 필요 (24시간 후)
     */
    @PostMapping("/refresh")
    public BaseResponse<AuthService.TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {

        // 수동 검증: 리프레시 토큰이 비어있는지 확인
        if (request == null || !StringUtils.hasText(request.getRefreshToken())) {
            return BaseResponse.fail(BaseResponseStatus.BAD_REQUEST, "리프레시 토큰은 필수입니다.");
        }

        return authService.refreshAccessToken(request);
    }
}