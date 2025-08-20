package com.passtival.backend.domain.lostfound.model.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FoundItemRequest {

	@NotBlank(message = "제목은 필수입니다.")
	private String title;

	@NotBlank(message = "위치는 필수입니다.")
	private String area;

	@NotNull(message = "시간 입력은 필수입니다.")
	@PastOrPresent(message = "습득 시간은 현재 이전이어야 합니다.")
	private LocalDateTime foundDateTime;

	private String imagePath;

}
