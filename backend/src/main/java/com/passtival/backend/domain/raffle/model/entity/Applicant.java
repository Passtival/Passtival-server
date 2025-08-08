package com.passtival.backend.domain.raffle.model.entity;

import com.passtival.backend.global.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "applicant")
@Getter
@Builder
@NoArgsConstructor // 기본 생성자 (JPA에서 필요)
@AllArgsConstructor // 모든 필드를 받는 생성자 (Builder 패턴용)
public class Applicant extends BaseEntity {

	@Id
	@GeneratedValue
	@Column(name = "applicant_id")
	private Long applicantId; // 신청자 ID

	@Column(name = "applicant_name", nullable = false, length = 15) // 박준선 (한글 5글자로 제한)
	private String applicantName; // 신청자 이름

	@Column(name = "student_id", nullable = false, length = 10) // 2021U2317 10개 제한
	private String studentId; // 학번

}
