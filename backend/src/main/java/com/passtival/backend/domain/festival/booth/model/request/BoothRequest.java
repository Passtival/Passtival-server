package com.passtival.backend.domain.festival.booth.model.request;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)   // Jackson 역직렬화용
@AllArgsConstructor                // Builder 내부적으로 사용
@Builder
public class BoothRequest {

	@NotBlank
	private final String name;

	private final String type;
	private final String department;
	private final LocalDateTime operatingStart;
	private final LocalDateTime operatingEnd;
	private final String location;
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
		private final int price;
	}
}
