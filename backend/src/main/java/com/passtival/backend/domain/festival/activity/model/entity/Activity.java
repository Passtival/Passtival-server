package com.passtival.backend.domain.festival.activity.model.entity;

import java.util.ArrayList;
import java.util.List;

import com.passtival.backend.domain.festival.booth.model.entity.Booth;
import com.passtival.backend.domain.festival.menu.model.entity.Menu;
import com.passtival.backend.global.common.model.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "activity")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Activity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "activity_id")
	private Long id;

	@Column(name = "activity_name", length = 30) // 글자수 30 제한
	private String name;

	@Column(name = "activity_introduction", length = 100) // 글자수 100 제한
	private String introduction;

	@Column(name = "activity_price")
	private Integer price;

	@Column(name = "activity_image_path")
	private String imagePath;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "booth_id", nullable = false)
	private Booth booth;

	public void setBooth(Booth booth) {
		this.booth = booth;
	}
}
