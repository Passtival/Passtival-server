package com.passtival.backend.domain.festival.booth.model.entity;

import java.time.LocalDateTime;

import com.passtival.backend.global.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "booth")
@Setter
@Getter
public class Booth extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "booth_id")
	private Long id;

	@Column(name = "booth_name")
	private String name;

	@Column(name = "booth_type")
	private String type;

	@Column(name = "booth_department")
	private String department;

	@Column(name = "booth_operating_start")
	private LocalDateTime operatingStart;

	@Column(name = "booth_operating_end")
	private LocalDateTime operatingEnd;

	@Column(name = "booth_location")
	private String location;

	@Column(name = "booth_info")
	private String info;

	@Column(name = "booth_image_path")
	private String imagePath;

	@Column(name = "booth_location_image_path")
	private String locationImagePath;

}
