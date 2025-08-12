package com.passtival.backend.global.auth.dto;



import lombok.*;

/**
 * 리프레시 토큰을 통한 액세스 토큰 갱신 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor  // JSON 역직렬화용 - public 유지 필요
@AllArgsConstructor(access = AccessLevel.PACKAGE)  // 패키지 내에서만 생성 가능
public class RefreshTokenRequest {

    private String refreshToken;

    public static RefreshTokenRequest of(String refreshToken) {
        return RefreshTokenRequest.builder()
                .refreshToken(refreshToken)
                .build();
    }
}