package com.passtival.backend.global.security.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AuthUserDto {
	private Long userId;
	private String socialId; // "kakao_1234567890"
	private String name; // "홍길동"
	private String role; // "ROLE_USER" (기본값)
}
