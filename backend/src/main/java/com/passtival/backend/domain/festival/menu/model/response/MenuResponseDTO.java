package com.passtival.backend.domain.festival.menu.model.response;

import com.passtival.backend.domain.festival.menu.model.entity.Menu;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MenuResponseDTO {

	private final String type;
	private final String name;
	private final String introduction;
	private final String imagePath;
	private final Integer price;

	public static MenuResponseDTO of(Menu menu) {

		if (menu == null) {
			// menu가 아예 존재하지 않을 경우
			throw new IllegalStateException("menu 정보가 존재하지 않습니다.");
		}
		return MenuResponseDTO.builder()
			.type(menu.getType())
			.name(menu.getName())
			.introduction(menu.getIntroduction())
			.imagePath(menu.getImagePath())
			.price(menu.getPrice())
			.build();
	}
}
