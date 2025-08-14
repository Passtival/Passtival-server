package com.passtival.backend.domain.festival.menu.model.entity;

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
@Table(name = "menu")
@Getter @Setter
public class Menu extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "menu_id")
	private Long id;

	@Column(name = "menu_type")
	private String type;

	@Column(name = "menu_name", length = 15) //글자수 15 제한
	private String name;

	@Column(name = "menu_introduction", length = 40) //글자수 200 제한
	private String introduction;

	@Column(name = "menu_price")
	private Integer price;

	@Column(name = "menu_image_path")
	private String imagePath;

}
