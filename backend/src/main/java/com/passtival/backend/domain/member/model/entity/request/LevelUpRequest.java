package com.passtival.backend.domain.member.model.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class LevelUpRequest {

	@NotBlank(message = "이름은 필수입니다.")
	private String name;

	@NotBlank(message = "학번은 필수입니다.")
	@Pattern(regexp = "^[A-Za-z0-9]+$", message = "학번은 영어와 숫자만 입력해주세요. 1234A5678 형식")
	private String studentId;

	@NotBlank(message = "인증키는 필수입니다.")
	private String authenticationKey;

	@NotNull(message = "레벨은 필수입니다.")
	@Positive(message = "레벨은 양수여야 합니다.")
	private Integer level;
}
