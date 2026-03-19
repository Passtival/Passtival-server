package com.passtival.backend.global.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FestivalErrorCode implements ErrorCode {
	PERFORMANCE_NOT_FOUND(404, "등록된 공연이 없습니다."),
	BOOTH_NOT_FOUND(404, "부스이름으로 등록된 데이터가 없습니다."),
	DUPLICATE_BOOTH_NAME(409, "이미 존재하는 부스이름입니다."),
	DUPLICATE_PERFORMANCE_TITLE(409, "이미 존재하는 공연 주제입니다.");

	private final int code;
	private final String message;
}
