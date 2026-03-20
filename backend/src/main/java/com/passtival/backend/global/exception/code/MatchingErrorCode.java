package com.passtival.backend.global.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MatchingErrorCode implements ErrorCode {
	MATCHING_TIME_INVALID(400, "매칭 신청은 매일 00:00부터 17:30까지만 가능합니다."),
	CONTACT_INFO_REQUIRED(400, "전화번호 또는 인스타그램 ID 중 적어도 하나는 입력해야 합니다."),
	GENDER_REQUIRED(400, "성별을 반드시 작성해야 합니다."),
	DUPLICATE_PHONE_NUMBER(409, "이미 사용중인 전화번호입니다."),
	DUPLICATE_INSTAGRAM_ID(409, "이미 사용중인 인스타그램 ID입니다."),
	MATCHING_IN_PROGRESS(409, "현재 매칭이 진행 중입니다. 내일 다시 시도해주세요."),
	ALREADY_APPLIED_MATCHING(409, "이미 매칭 신청을 완료하였습니다."),
	MATCHING_RESULT_NOT_FOUND(404, "오늘 매칭 결과가 없습니다.");

	private final int code;
	private final String message;
}
