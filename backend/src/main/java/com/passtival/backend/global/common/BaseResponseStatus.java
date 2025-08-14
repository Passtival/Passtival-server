package com.passtival.backend.global.common;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {

	// 200: 성공
	OK(true, 200, "요청에 성공하였습니다."),

	// 400: 클라이언트 오류
	BAD_REQUEST(false, 400, "잘못된 요청입니다."),
	INVALID_REQUEST(false, 400, "요청 조건이 충족되지 않습니다."),
	VALIDATION_ERROR(false, 400, "입력값 검증에 실패했습니다."),

	// 401: 인증 오류
	UNAUTHORIZED(false, 401, "인증에 실패했습니다."),
	TOKEN_EXPIRED(false, 401, "토큰이 만료되었습니다."),
	TOKEN_INVALID(false, 401, "유효하지 않은 토큰입니다."),
	LOGIN_REQUIRED(false, 401, "로그인이 필요합니다."),

	// 403: 접근 권한 오류
	ACCESS_DENIED(false, 403, "해당 리소스에 접근할 수 없습니다."),

	// 404: 리소스 없음
	NOT_FOUND(false, 404, "요청한 리소스를 찾을 수 없습니다."),
	PRIZE_NOT_FOUND(false, 404, "해당 ID의 상품을 찾을 수 없습니다."),
	AUTH_KEY_NOT_FOUND(false, 404, "등록된 인증키가 없습니다."),

	// 409: 충돌
	DUPLICATE_REQUEST(false, 409, "이미 처리된 요청입니다."),
	DUPLICATE_APPLICANT(false, 409, "이미 신청한 학번입니다."),

	// 422: 처리 불가능한 엔티티 (비즈니스 로직 오류)
	INVALID_AUTH_KEY(false, 422, "인증키가 일치하지 않습니다."),
	SAME_AUTH_KEY(false, 422, "기존 인증키와 동일한 키로는 변경할 수 없습니다."),

	// 500: 서버 오류
	INTERNAL_SERVER_ERROR(false, 500, "서버에서 오류가 발생하였습니다."),
	DATABASE_ERROR(false, 500, "데이터베이스 오류가 발생했습니다.");




	private final boolean isSuccess; // 요청 성공 여부
	private final int code; // 응답 코드
	private final String message; // 응답 메세지

	private BaseResponseStatus(boolean isSuccess, int code, String message) {
		this.isSuccess = isSuccess;
		this.code = code;
		this.message = message;
	}
}
