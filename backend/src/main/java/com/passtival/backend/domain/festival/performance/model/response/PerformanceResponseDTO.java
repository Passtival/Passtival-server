package com.passtival.backend.domain.festival.performance.model.response;

import java.time.LocalDateTime;

import com.passtival.backend.domain.festival.performance.model.entity.Performance;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PerformanceResponseDTO {

	private final String title;
	private final String artist;
	private final LocalDateTime startAt;
	private final LocalDateTime endAt;
	private final String area;
	private final String imagePath;
	private final String introduction;
	private final String info;

	public static PerformanceResponseDTO of(Performance performance) {

		if (performance == null) {
			// performance가 아예 존재하지 않을 경우
			throw new IllegalArgumentException("performance 정보가 존재하지 않습니다.");
		}
		return PerformanceResponseDTO.builder()
			.title(performance.getTitle())
			.artist(performance.getArtist())
			.startAt(performance.getStartAt())
			.endAt(performance.getEndAt())
			.area(performance.getArea())
			.imagePath(performance.getImagePath())
			.introduction(performance.getIntroduction())
			.info(performance.getInfo())
			.build();
	}
}
