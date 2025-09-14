package com.passtival.backend.domain.festival.booth.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.passtival.backend.domain.festival.activity.model.entity.Activity;
import com.passtival.backend.domain.festival.menu.model.entity.Menu;
import com.passtival.backend.global.common.model.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "booth")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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

	// cascade: 엔티티 생명주기 전이 기능 → 부모 저장/삭제 시 자식도 같이 저장/삭제
	// orphanRemoval: 관계 끊어지면 자식 자동 삭제
	@Builder.Default
	@OneToMany(mappedBy = "booth", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Menu> menus = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "booth", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Activity> activities = new ArrayList<>();

	public void addMenu(Menu menu) {
		this.menus.add(menu);
		menu.setBooth(this);
	}

	public void addActivity(Activity activity) {
		this.activities.add(activity);
		activity.setBooth(this);
	}

}
