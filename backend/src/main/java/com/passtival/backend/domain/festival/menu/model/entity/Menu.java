package com.passtival.backend.domain.festival.menu.model.entity;

import com.passtival.backend.domain.festival.booth.model.entity.Booth;
import com.passtival.backend.domain.festival.performance.model.entity.Performance;
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
import lombok.Setter;

@Entity
@Table(name = "menu")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "booth_id", nullable = false)
	private Booth booth;

	public void setBooth(Booth booth) {
		this.booth = booth;
	}

}
