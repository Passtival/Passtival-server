package com.passtival.backend.domain.matching.model.request;

import com.passtival.backend.domain.matching.model.enums.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor  // Jackson 역직렬화를 위한 기본 생성자
public class MemberOnboardingRequest {
	@NotNull(message = "성별은 필수입니다.")
	private Gender gender;

	@NotBlank(message = "전화번호는 필수입니다.")
	@Size(max = 20, message = "전화번호에 지정된 길이를 초과할 수 없습니다.")
	private String phoneNumber;

	@Pattern(regexp = "^[a-zA-Z0-9._]*$", message = "인스타그램 ID는 영문, 숫자, '.', '_'만 사용 가능합니다.")
	@Size(max = 35, message = "인스타그램 ID는 35자를 초과할 수 없습니다.")
	private String instagramId;
}