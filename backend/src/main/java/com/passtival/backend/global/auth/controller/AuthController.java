package com.passtival.backend.global.auth.controller;

import com.passtival.backend.global.auth.dto.RefreshTokenRequest;
import com.passtival.backend.global.auth.dto.TokenResponseDto;
import com.passtival.backend.global.auth.service.AuthService;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 API를 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/refresh")
    public BaseResponse<TokenResponseDto> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {

        // 수동 검증: 리프레시 토큰이 비어있는지 확인
        if (request == null || !StringUtils.hasText(request.getRefreshToken())) {
            return BaseResponse.fail(BaseResponseStatus.BAD_REQUEST, "리프레시 토큰은 필수입니다.");
        }

        return authService.refreshAccessToken(request);
    }
}