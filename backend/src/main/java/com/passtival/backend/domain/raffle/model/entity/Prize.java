package com.passtival.backend.domain.raffle.model.entity;

import com.passtival.backend.global.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "prize")
public class Prize extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "prize_id")
	private Long prizeId;

	@Column(name = "prize_image_path")
	private String prizeImagePath;

	@Column(name = "prize_name")
	private String prizeName;

}
