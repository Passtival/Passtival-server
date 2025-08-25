package com.passtival.backend.domain.festival.booth.model.response;

import java.time.LocalDateTime;

import com.passtival.backend.domain.festival.booth.model.entity.Booth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoothResponse {

	private final Long id;
	private final String name;
	private final String type;
	private final String department;
	private final LocalDateTime operatingStart;
	private final LocalDateTime operatingEnd;
	private final String location;
	private final String info;
	private final String imagePath;
	private final String locationImagePath;

	/**
	 * Booth 엔티티를 BoothResponseDTO로 변환하는 정적 팩토리 메서드
	 *
	 * @param booth Booth 엔티티
	 * @return BoothResponseDTO 객체
	 * @throws IllegalArgumentException booth가 null인 경우
	 */
	public static BoothResponse of(Booth booth) {

		return BoothResponse.builder()
			.id(booth.getId())
			.name(booth.getName())
			.type(booth.getType())
			.department(booth.getDepartment())
			.operatingStart(booth.getOperatingStart())
			.operatingEnd(booth.getOperatingEnd())
			.location(booth.getLocation())
			.info(booth.getInfo())
			.imagePath(booth.getImagePath())
			.locationImagePath(booth.getLocationImagePath())
			.build();
	}
}
