package com.passtival.backend.domain.matching.model.response;

import com.passtival.backend.domain.matching.model.enums.Gender;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchingApplicantResponse {
	private Long MemberId;

	private String MemberName;

	private Gender MemberGender;

	private String MemberPhoneNumber;

	private String MemberInstagramId;
}
