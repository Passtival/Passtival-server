package com.passtival.backend.domain.member.model.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class LevelUpRequest {

	@NotBlank(message = "이름은 필수입니다.")
	@Pattern(regexp = "^[가-힣]{2,4}$", message = "이름은 2~4글자의 한글로 입력해주세요.")
	private String name;

	@NotBlank(message = "학번은 필수입니다.")
	@Pattern(regexp = "^\\d{4}[A-Z]\\d{4}$", message = "학번은 2021U2317 형식으로 입력해주세요.")
	private String studentId;

	@NotBlank(message = "인증키는 필수입니다.")
	private String authenticationKey;

	@NotNull(message = "레벨은 필수입니다.")
	@Positive(message = "레벨은 양수여야 합니다.")
	private Integer level;
}
