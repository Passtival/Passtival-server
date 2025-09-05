package com.passtival.backend.domain.admin.model.response;

import lombok.Getter;

@Getter
public class WinnerResponse {

	private final String name;
	private final String studentId;

	public WinnerResponse(String name, String studentId) {
		this.name = name;
		this.studentId = studentId;
	}
}
