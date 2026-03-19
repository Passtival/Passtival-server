package com.passtival.backend.global.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
	TOKEN_EXPIRED(401, "토큰이 만료되었습니다."),
	TOKEN_INVALID(401, "유효하지 않은 토큰입니다."),
	LOGIN_REQUIRED(401, "로그인이 필요합니다."),
	INVALID_TOKEN_FORMAT(400, "잘못된 형태의 토큰입니다."),
	UNSUPPORTED_PROVIDER(400, "지원하지 않는 제공자입니다."),
	OAUTH2_PROCESSING_ERROR(401, "OAuth2 인증 처리 중 오류가 발생했습니다."),
	SOCIAL_ID_NOTFOUND(401, "소셜 ID가 존재하지 않습니다."),
	SOCIAL_ID_VERIFICATION_FAILED(409, "소셜 ID 검증 중 오류가 발생했습니다"),
	MEMBER_PROCESSING_ERROR(500, "회원 정보 처리 중 오류가 발생했습니다."),
	NEW_MEMBER_PROCESSING_ERROR(500, "신규 회원 정보 처리 중 오류가 발생했습니다"),
	MEMBER_DETAILS_CREATION_ERROR(500, "사용자 인증 정보 생성 중 오류가 발생했습니다.");

	private final int code;
	private final String message;
}
