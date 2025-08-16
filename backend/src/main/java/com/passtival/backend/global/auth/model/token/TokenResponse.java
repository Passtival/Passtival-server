package com.passtival.backend.global.auth.model.token;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

/**
 * 토큰 응답 DTO
 * Access Token은 항상 포함되며, Refresh Token은 필요에 따라 포함될 수 있음.
 */
@Getter
@Builder
// Json으로 변환 시 null인 필드는 제외하는 어노테이션
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenResponse {
	private final String accessToken;
	private final String refreshToken;
}