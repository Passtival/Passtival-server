package com.passtival.backend.global.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
	MEMBER_NOT_FOUND(404, "회원을 찾을 수 없습니다."),
	INCOMPLETE_MEMBER_INFO(400, "회원 정보가 불완전합니다."),
	INVALID_AUTH_KEY(422, "인증키가 일치하지 않습니다."),
	NOT_FOUND_AUTH_KEY(500, "모든 인증키를 소모했습니다."),
	INVALID_LEVEL(400, "인증키 level과 사용자 요청 레벨이 불일치합니다.");

	private final int code;
	private final String message;
}
