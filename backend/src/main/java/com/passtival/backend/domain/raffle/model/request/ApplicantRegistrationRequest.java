package com.passtival.backend.domain.raffle.model.request;

import lombok.Getter;

@Getter
public class ApplicantRegistrationRequest {
	private String applicantName; // 신청자 이름
	private String studentId; // 신청자 학번
	private String key; // 인증키
}
