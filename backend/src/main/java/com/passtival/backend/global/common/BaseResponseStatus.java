package com.passtival.backend.global.common;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {

	// 200: 성공
	OK(true, 200, "요청에 성공하였습니다."),

	// 400: 클라이언트 오류
	BAD_REQUEST(false, 400, "잘못된 요청입니다."),
	TOKEN_EXPIRED(false, 401, "토큰이 만료되었습니다."),
	TOKEN_INVALID(false, 401, "유효하지 않은 토큰입니다."),
	LOGIN_REQUIRED(false, 401, "로그인이 필요합니다."),
	ACCESS_DENIED(false, 403, "해당 리소스에 접근할 수 없습니다."),

	// 추가한 오류 메시지
	NAME_INVALID(false, 404, "해당 이름과 일치하는 데이터가 존재하지 않습니다."),
	DATA_NULL(false, 404, "데이터가 존재하지 않습니다."),

	// 500: 서버 오류
	INTERNAL_SERVER_ERROR(false, 500, "서버에서 오류가 발생하였습니다."),
	DATABASE_ERROR(false, 500, "데이터베이스 오류가 발생했습니다."),
	IO_ERROR(false, 500, "입출력 처리 중 오류가 발생했습니다.");

	private final boolean isSuccess; // 요청 성공 여부
	private final int code; // 응답 코드
	private final String message; // 응답 메세지

	private BaseResponseStatus(boolean isSuccess, int code, String message) {
		this.isSuccess = isSuccess;
		this.code = code;
		this.message = message;
	}
}
