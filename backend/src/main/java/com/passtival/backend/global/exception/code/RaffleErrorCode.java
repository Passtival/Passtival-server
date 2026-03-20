package com.passtival.backend.global.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RaffleErrorCode implements ErrorCode {
	PRIZES_NOT_FOUND(404, "등록된 상품이 없습니다."),
	DUPLICATE_APPLICANT(409, "이미 신청한 학번입니다.");

	private final int code;
	private final String message;
}
