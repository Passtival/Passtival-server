package com.passtival.backend.domain.lostfound.model.entity;

import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import com.passtival.backend.global.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class FoundItem extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title; // 습득물 제목

	@Column(nullable = false)
	private String area; // 습득 장소

	@Column(nullable = false)
	private LocalDateTime foundDateTime; // 습득 일시

	private String imagePath; // 이미지 경로

	@Builder
	public FoundItem(String title, String area, LocalDateTime foundDateTime, String imagePath) {
		this.title = title;
		this.area = area;
		this.foundDateTime = foundDateTime;
		this.imagePath = imagePath;
	}
}
