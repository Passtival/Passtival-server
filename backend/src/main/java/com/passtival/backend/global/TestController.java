package com.passtival.backend.global;


import com.passtival.backend.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * JWT 토큰 인증 테스트를 위한 컨트롤러
 * Access Token이 올바르게 검증되는지 확인하는 용도
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    /**
     * JWT 토큰 인증 테스트 API
     * GET /api/test/ping
     *
     * @return BaseResponse<String> 인증 성공 시 "pong" 메시지 반환
     *
     * 목적:
     * 1. Access Token이 올바르게 검증되는지 확인
     * 2. JWT 인증 필터가 정상 작동하는지 검증
     * 3. 만료된 토큰, 잘못된 토큰 등의 에러 케이스 테스트
     */
    @GetMapping("/ping")
    public BaseResponse<String> ping() {
        return BaseResponse.success("pong - JWT 인증 성공!");
    }
}