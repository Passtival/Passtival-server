package com.passtival.backend.domain.festival.performance.model.entity;

import java.time.LocalDateTime;

import com.passtival.backend.global.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "performance")
@Getter
@Builder
public class Performance extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "performance_id")
	private Long id;

	@Column(name = "performance_title")
	private String title;

	@Column(name = "performance_artist")
	private String artist;

	@Column(name = "performance_start_at")
	private LocalDateTime startAt;

	@Column(name = "performance_end_at")
	private LocalDateTime endAt;

	@Column(name = "performance_area")
	private String area;

	@Column(name = "performance_image_path")
	private String imagePath;

	@Column(name = "performance_introduction")
	private String introduction;

	@Column(name = "performance_info")
	private String info;
}
