package com.passtival.backend.domain.raffle.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ApplicantRegistrationRequest {

	@NotBlank(message = "신청자 이름은 필수입니다.")
	@Pattern(regexp = "^[가-힣]{2,5}$", message = "신청자 이름은 2~5글자의 한글로 입력해주세요.")
	private String applicantName; // 신청자 이름

	@NotBlank(message = "학번은 필수입니다.")
	@Pattern(regexp = "^[A-Za-z0-9]$", message = "학번은 영어와 숫자만 입력해주세요.")
	private String studentId;

	@NotBlank(message = "인증키는 필수입니다.")
	@Size(min = 4, max = 20, message = "인증키는 4자 이상 20자 이하여야 합니다.")
	private String authenticationKey; // 인증키
}
