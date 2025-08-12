package com.passtival.backend.domain.festival.performance.model.response;

import java.time.LocalDateTime;

import com.passtival.backend.domain.festival.performance.model.entity.Performance;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PerformanceResponse {

	private final String title;
	private final String artist;
	private final LocalDateTime startAt;
	private final LocalDateTime endAt;
	private final String imagePath;
	private final String introduction;
	private final Integer day;

	public static PerformanceResponse of(Performance performance) {

		if (performance == null) {
			// performance가 아예 존재하지 않을 경우
			throw new IllegalArgumentException("performance 정보가 존재하지 않습니다.");
		}
		return PerformanceResponse.builder()
			.title(performance.getTitle())
			.artist(performance.getArtist())
			.startAt(performance.getStartAt())
			.endAt(performance.getEndAt())
			.imagePath(performance.getImagePath())
			.introduction(performance.getIntroduction())
			.day(performance.getDay())
			.build();
	}
}
