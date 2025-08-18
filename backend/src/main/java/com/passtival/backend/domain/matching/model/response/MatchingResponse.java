package com.passtival.backend.domain.matching.model.response;

import lombok.Builder;
import lombok.Getter;

//미팅 결과를 전달하기 위한 Dto
@Getter
@Builder
public class MatchingResponse {

	// 내 정보
	private final MemberInfo myInfo;

	// 상대방 정보
	private final MemberInfo partnerInfo;

	// 매칭 날짜
	private final String matchingDate;

	@Getter
	@Builder
	public static class MemberInfo {
		private final String phoneNumber;
		private final String instagramId;
	}
}