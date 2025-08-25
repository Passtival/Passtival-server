package com.passtival.backend.domain.festival.performance.model.request;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PerformanceRequest {

	@NotBlank(message = "공연 이름은 비워둘 수 없습니다.")
	private String title;

	@NotBlank(message = "공연 진행 학과,동아리 이름은 비워둘 수 없습니다.")
	private String artist;

	@NotNull(message = "공연 시작시간은 비워둘 수 없습니다.")
	private LocalDateTime startTime;

	@NotNull(message = "공연 끝나는시간은 비워둘 수 없습니다.")
	private LocalDateTime endTime;

	@NotBlank(message = "공연 위치는 비워둘 수 없습니다.")
	private String area;

	private String imagePath;
	private String introduction;
	private Integer day;

	private List<SongRequest> songs;

	@Getter
	@NoArgsConstructor
	public static class SongRequest {
		@NotBlank
		private String singer;

		@NotBlank
		private String title;
	}
}
