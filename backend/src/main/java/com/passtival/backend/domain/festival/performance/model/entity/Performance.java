package com.passtival.backend.domain.festival.performance.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.passtival.backend.global.common.model.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "performance")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

	@Column(name = "performance_start_time")
	private LocalDateTime startTime;

	@Column(name = "performance_end_time")
	private LocalDateTime endTime;

	@Column(name = "performance_area")
	private String area;

	@Column(name = "performance_image_path")
	private String imagePath;

	@Column(name = "performance_introduction")
	private String introduction;

	@Builder.Default
	@OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Song> songs = new ArrayList<>();

	// 일차를 구분해주는 속성
	@Column(name = "performance_day")
	private Integer day;

	public void addSong(Song song) {
		this.songs.add(song);
		song.setPerformance(this);
	}

}
