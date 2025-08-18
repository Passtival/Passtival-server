package com.passtival.backend.domain.matching.model.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MatchingRequest {
	@Size(max = 35, message = "인스타그램 ID의 최대 길이를 초과할 수 없습니다.")
	@Pattern(regexp = "^[a-zA-Z0-9._]*$", message = "인스타그램 ID는 영문, 숫자, '.', '_'만 사용 가능합니다.")
	private String instagramId;
}
