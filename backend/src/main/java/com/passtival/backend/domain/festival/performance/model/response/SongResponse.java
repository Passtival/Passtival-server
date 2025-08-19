package com.passtival.backend.domain.festival.performance.model.response;

import com.passtival.backend.domain.festival.performance.model.entity.Song;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SongResponse {

	private final String singer;
	private final String title;

	public static SongResponse from(Song song) {

		return SongResponse.builder()
			.singer(song.getSinger())
			.title(song.getTitle())
			.build();
	}
}
