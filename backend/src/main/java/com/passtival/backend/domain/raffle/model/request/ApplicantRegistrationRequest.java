package com.passtival.backend.domain.raffle.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ApplicantRegistrationRequest {

	@NotBlank
	private String applicantName; // 신청자 이름
	@NotBlank
	private String studentId; // 신청자 학번
	@NotBlank
	private String key; // 인증키
}
