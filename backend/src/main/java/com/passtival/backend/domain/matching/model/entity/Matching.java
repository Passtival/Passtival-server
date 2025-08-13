package com.passtival.backend.domain.matching.model.entity;

import java.time.LocalDate;
import java.time.ZoneId;

import com.passtival.backend.global.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//미팅 테이블 엔티티 매일 6시 생성
@Entity
@Table(name = "matching")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Matching extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long resultId;

	@Column(name = "male_id", nullable = false)
	private Long maleId;

	@Column(name = "female_id", nullable = false)
	private Long femaleId;

	@Column(name = "matching_date", nullable = false)
	private LocalDate matchingDate;

	public static Matching createMatching(Long maleId, Long femaleId) {
		return Matching.builder()
			.maleId(maleId)
			.femaleId(femaleId)
			.matchingDate(LocalDate.now(ZoneId.of("Asia/Seoul")))
			.build();
	}
}
