package com.passtival.backend.global.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LostFoundErrorCode implements ErrorCode {
	FOUND_ITEM_NOT_FOUND(404, "해당 ID의 발견된 분실물을 찾을 수 없습니다.");

	private final int code;
	private final String message;
}
