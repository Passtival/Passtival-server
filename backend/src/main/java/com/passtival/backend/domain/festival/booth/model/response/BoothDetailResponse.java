package com.passtival.backend.domain.festival.booth.model.response;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.passtival.backend.domain.festival.booth.model.entity.Booth;
import com.passtival.backend.domain.festival.menu.model.entity.Menu;
import com.passtival.backend.domain.festival.menu.model.response.MenuResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoothDetailResponse {

	private Long id;
	private String name;
	private String type;
	private String department;
	private LocalDateTime operatingStart;
	private LocalDateTime operatingEnd;
	private String location;
	private String info;
	private String imagePath;
	private String locationImagePath;

	// 부스에 포함된 메뉴 리스트 (1:N 대응)
	private List<MenuResponse> menus;

	public static BoothDetailResponse of(Booth booth) {

		List<Menu> menuEntities;

		List<MenuResponse> menuResponses = Optional.ofNullable(booth.getMenus())
			.orElse(Collections.emptyList())
			.stream()
			.filter(Objects::nonNull)
			.map(MenuResponse::from)
			.collect(Collectors.toList());

		return BoothDetailResponse.builder()
			.id(booth.getId())
			.name(booth.getName())
			.type(booth.getType())
			.department(booth.getDepartment())
			.operatingStart(booth.getOperatingStart())
			.operatingEnd(booth.getOperatingEnd())
			.location(booth.getLocation())
			.info(booth.getInfo())
			.imagePath(booth.getImagePath())
			.menus(menuResponses)
			.build();
	}

}
