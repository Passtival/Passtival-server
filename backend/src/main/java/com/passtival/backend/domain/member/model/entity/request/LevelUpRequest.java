package com.passtival.backend.domain.member.model.entity.request;

import lombok.Getter;

@Getter
public class LevelUpRequest {
	private String name;
	private String studentId;
	private String authenticationKey;
	private Integer level;
}
