package com.passtival.backend.domain.festival.performance.model.entity;

import com.passtival.backend.global.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "song")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 권장
@AllArgsConstructor
public class Song extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "song_id")
	private Long id;

	@Column(name = "song_singer")
	private String singer;

	@Column(name = "song_title")
	private String title;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "performance_id", nullable = false)
	private Performance performance;

	public void setPerformance(Performance performance) {
		this.performance = performance;
	}
}
