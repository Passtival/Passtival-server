package com.passtival.backend.global.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdminErrorCode implements ErrorCode {
	AUTH_KEY_NOT_FOUND(404, "등록된 인증키가 없습니다."),
	ADMIN_LOGIN_FAILED(401, "관리자 ID 혹은 인증키가 올바르지 못합니다.");

	private final int code;
	private final String message;
}
