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
	private final LocalDateTime startTime;
	private final LocalDateTime endTime;
	private final String imagePath;
	private final String introduction;
	private final Integer day;

	public static PerformanceResponse of(Performance performance) {

		return PerformanceResponse.builder()
			.title(performance.getTitle())
			.artist(performance.getArtist())
			.startTime(performance.getStartTime())
			.endTime(performance.getEndTime())
			.imagePath(performance.getImagePath())
			.introduction(performance.getIntroduction())
			.day(performance.getDay())
			.build();
	}
}
