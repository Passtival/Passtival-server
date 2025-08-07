package com.passtival.backend.domain.raffle.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "applicant")
@Getter
public class Applicant {

	@Id
	@GeneratedValue
	@Column(name = "applicant_id")
	private Long applicantId; // 신청자 ID

	@Column(name = "applicant_name", nullable = false, length = 15) // 박준선 (한글 5글자로 제한)
	private String applicantName; // 신청자 이름

	@Column(name = "student_id", nullable = false, length = 10) // 2021U2317 10개 제한
	private String studentId; // 학번

}
