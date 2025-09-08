package com.passtival.backend.domain.admin.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor
public class Winner {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String studentId;

	private int day; // 4면 premium

	@Builder
	public Winner(String name, String studentId, int day) {
		this.name = name;
		this.studentId = studentId;
		this.day = day;
	}

}
