package com.passtival.backend.domain.raffle.model.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberRaffleProfileResponse {

	private final Integer level;
	private final String name;
	private final String studentId;

	@Builder
	public MemberRaffleProfileResponse(Integer level, String name, String studentId) {
		this.level = level;
		this.name = name;
		this.studentId = studentId;
	}
}
