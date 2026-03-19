package com.passtival.backend.global.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode implements ErrorCode {
	BAD_REQUEST(400, "잘못된 요청입니다."),
	INVALID_REQUEST(400, "요청 조건이 충족되지 않습니다."),
	NOT_FOUND(404, "요청한 리소스를 찾을 수 없습니다."),
	INTERNAL_SERVER_ERROR(500, "서버에서 오류가 발생하였습니다."),
	DATABASE_ERROR(500, "데이터베이스 오류가 발생했습니다."),
	REQUEST_BODY_EMPTY(404, "요청 데이터가 없습니다."),
	CRON_ERROR(500, "잘못된 cron 표현식 형식입니다.");

	private final int code;
	private final String message;
}
