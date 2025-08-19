package com.passtival.backend.domain.matching.model.request;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.passtival.backend.domain.matching.model.enums.Gender;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 왜 member
@Getter
@NoArgsConstructor  // Jackson 역직렬화를 위한 기본 생성자
public class MemberPatchRequest {

	/* Optional을 통해서 안 보내는 것과 null을 구분
	 * nulls = Nulls.FAIL를 통해서 명시적으로 null를 방어 null이 오면 400 오류 나옴
	 * 성병 제외 비우고 싶으면 ""를 전달 해야 한다.
	 */
	@JsonSetter(nulls = Nulls.FAIL)
	//enum 이라서 @Size, @Pattern 미사용
	private Optional<Gender> gender = Optional.empty();

	@JsonSetter(nulls = Nulls.FAIL)
	@Pattern(
		regexp = "^(?:$|010-\\\\d{4}-\\\\d{4})$",
		message = "전화번호는 빈 값이거나 다음 형식여야 합니다: '010-1234-5678'"
	)
	@Size(
		// 빈 문자열("") 허용
		min = 0,
		// "010-1234-5678"은 정확히 13자
		max = 13,
		message = "전화번호는 빈 문자열이거나 길이 13자여야 합니다."
	)
	private Optional<String> phoneNumber = Optional.empty();

	@JsonSetter(nulls = Nulls.FAIL)
	@Pattern(
		regexp = "^[a-zA-Z0-9._]*$",
		message = "인스타그램 ID는 영문, 숫자, '.', '_'만 사용 가능합니다."
	)
	@Size(
		min = 0,
		max = 30,
		message = "인스타그램 ID는 빈 문자열이거나 길이 30자 내여야 합니다."
	)
	private Optional<String> instagramId = Optional.empty();
}