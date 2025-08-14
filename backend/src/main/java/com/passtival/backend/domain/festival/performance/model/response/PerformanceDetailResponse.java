package com.passtival.backend.domain.festival.performance.model.response;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.passtival.backend.domain.festival.performance.model.entity.Performance;
import com.passtival.backend.domain.festival.performance.model.entity.Song;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PerformanceDetailResponse {

	private final String title;
	private final String artist;
	private final LocalDateTime startTime;
	private final LocalDateTime endTime;
	private final String area;
	private final String imagePath;
	private final String introduction;
	private final Integer day;

	// 공연에 포함된 곡 리스트 (1:N 대응)
	private final List<SongResponse> songs;

	public static PerformanceDetailResponse of(Performance performance) {

		if (performance == null) {
			// performance가 아예 존재하지 않을 경우
			throw new IllegalArgumentException("performance 정보가 존재하지 않습니다.");
		}

		List<Song> songEntities;

		//Performance에 getSongs()가 있으면 그걸 쓰고, 없으면 그냥 빈 리스트로 처리
		try {
			songEntities = performance.getSongs();
		} catch (NoSuchMethodError e) {
			songEntities = Collections.emptyList();
		}

		List<SongResponse> songResponses = (songEntities == null ? Collections.<Song>emptyList() : songEntities)
			.stream()
			.filter(Objects::nonNull)
			.map(SongResponse::from)
			.collect(Collectors.toList());

		return PerformanceDetailResponse.builder()
			.title(performance.getTitle())
			.artist(performance.getArtist())
			.startTime(performance.getStartTime())
			.endTime(performance.getEndTime())
			.area(performance.getArea())
			.imagePath(performance.getImagePath())
			.introduction(performance.getIntroduction())
			.day(performance.getDay())
			.songs(songResponses)
			.build();
	}
}
