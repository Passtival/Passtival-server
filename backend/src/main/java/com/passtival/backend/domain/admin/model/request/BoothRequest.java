package com.passtival.backend.domain.admin.model.request;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
	@FutureOrPresent(message = "운영 시작 시간은 현재 이후여야 합니다.")
	private final LocalDateTime operatingStart;

	@NotNull(message = "운영 종료 시간은 필수 입력값입니다.")
	@FutureOrPresent(message = "운영 종료 시간은 현재 이후여야 합니다.")
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

		@NotBlank(message = "부스 유형은 필수 입력값입니다.")
		private final String type;

		@Size(max = 15, message = "메뉴 이름은 최대 15자 이내여야 합니다.")
		private final String name;

		@Size(max = 15, message = "메뉴 설명은 최대 40자 이내여야 합니다.")
		private final String introduction;

		private final String imagePath;

		@PositiveOrZero(message = "메뉴 가격은 0원 이상이어야 합니다.")
		private final Integer price;
	}

	// 운영 시작 <= 종료 검증
	@AssertTrue(message = "운영 시작 시간은 종료 시간보다 빠르거나 같아야 합니다.")
	public boolean isValidOperatingTime() {
		return operatingStart != null && operatingEnd != null && !operatingStart.isAfter(operatingEnd);
	}
}
