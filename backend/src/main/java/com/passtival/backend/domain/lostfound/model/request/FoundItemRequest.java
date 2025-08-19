package com.passtival.backend.domain.lostfound.model.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FoundItemRequest {

	@NotBlank(message = "제목은 필수입니다.")
	private String title;

	@NotBlank(message = "위치는 필수입니다.")
	private String area;

	@NotBlank(message = "시간 입력은 필수입니다.")
	private LocalDateTime foundDateTime;

	private String imagePath;

}
