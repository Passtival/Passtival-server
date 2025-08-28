package com.passtival.backend.domain.festival.performance.model.request;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PerformanceRequest {

	@NotBlank(message = "공연 이름은 비워둘 수 없습니다.")
	@Size(max = 15, message = "공연 이름은 최대 15자 이내여야 합니다.")
	private String title;

	@NotBlank(message = "공연 진행 학과,동아리 이름은 비워둘 수 없습니다.")
	@Size(max = 20, message = "공연 진행 학과, 동아리 이름은 최대 20자 이내여야 합니다.")
	private String artist;

	@NotNull(message = "공연 시작시간은 비워둘 수 없습니다.")
	@FutureOrPresent(message = "공연 시작시간은 현재 이후여야 합니다.")
	private LocalDateTime startTime;

	@NotNull(message = "공연 끝나는시간은 비워둘 수 없습니다.")
	@FutureOrPresent(message = "공연 종료시간은 현재 이후여야 합니다.")
	private LocalDateTime endTime;

	@NotBlank(message = "공연 위치는 비워둘 수 없습니다.")
	private String area;

	private String imagePath;
	private String introduction;

	@Min(value = 1, message = "공연 일차는 1 이상이어야 합니다.")
	@Max(value = 3, message = "공연 일차는 최대 3일까지 허용됩니다.")
	private Integer day;

	@NotNull(message = "노래 목록은 비워둘 수 없습니다.")
	@Size(min = 1, message = "공연에는 최소 1곡 이상의 노래가 필요합니다.")
	private List<SongRequest> songs;

	@Getter
	@NoArgsConstructor
	public static class SongRequest {
		@NotBlank(message = "가수 이름은 비워둘 수 없습니다.")
		private String singer;

		@NotBlank(message = "노래 제목은 비워둘 수 없습니다.")
		private String title;
	}

	// 공연 시작이 종료보다 늦지 않도록 추가 검증
	@AssertTrue(message = "공연 시작시간은 종료시간보다 빠르거나 같아야 합니다.")
	public boolean isValidTimeRange() {
		return startTime != null && endTime != null && !startTime.isAfter(endTime);
	}
}
