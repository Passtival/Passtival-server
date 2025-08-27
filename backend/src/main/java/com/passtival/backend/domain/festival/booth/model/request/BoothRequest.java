package com.passtival.backend.domain.festival.booth.model.request;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)   // Jackson 역직렬화용
@AllArgsConstructor                // Builder 내부적으로 사용
@Builder
public class BoothRequest {

	@Column(unique = true)
	@NotBlank(message = "부스 이름은 필수 입력값입니다.")
	@Size(max = 15, message = "부스 이름은 최대 15자 이내여야 합니다.")
	private final String name;

	@NotBlank(message = "부스 유형은 필수 입력값입니다.")
	private final String type;

	private final String department;

	@NotNull(message = "운영 시작 시간은 필수 입력값입니다.")
	private final LocalDateTime operatingStart;

	@NotNull(message = "운영 종료 시간은 필수 입력값입니다.")
	private final LocalDateTime operatingEnd;

	@NotBlank(message = "부스 위치는 필수 입력값입니다.")
	private final String location;

	@Size(max = 200, message = "부스 소개는 최대 200자 이내여야 합니다.")
	private final String info;

	private final String imagePath;

	private final String locationImagePath;

	private final List<MenuRequest> menus;

	@Getter
	@NoArgsConstructor(force = true)
	@AllArgsConstructor
	@Builder
	public static class MenuRequest {
		private final String type;
		private final String name;
		private final String introduction;
		private final String imagePath;
		private final Integer price;
	}
}
