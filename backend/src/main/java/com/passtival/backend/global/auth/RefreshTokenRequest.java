package com.passtival.backend.global.auth;



import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 리프레시 토큰을 통한 액세스 토큰 갱신 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class RefreshTokenRequest {

    private String refreshToken;
}