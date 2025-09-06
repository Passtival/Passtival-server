package com.passtival.backend.domain.festival.menu.model.response;

import com.passtival.backend.domain.festival.menu.model.entity.Menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MenuResponse {

	private final Long id;
	private final String type;
	private final String name;
	private final String introduction;
	private final String imagePath;
	private final Integer price;

	public static MenuResponse from(Menu menu) {

		return MenuResponse.builder()
			.id(menu.getId())
			.type(menu.getType())
			.name(menu.getName())
			.introduction(menu.getIntroduction())
			.imagePath(menu.getImagePath())
			.price(menu.getPrice())
			.build();
	}

	public static MenuResponse of(Menu menu) {

		return MenuResponse.builder()
			.id(menu.getId())
			.type(menu.getType())
			.name(menu.getName())
			.introduction(menu.getIntroduction())
			.imagePath(menu.getImagePath())
			.price(menu.getPrice())
			.build();
	}
}
