package com.passtival.backend.domain.festival.booth.model.response;

import com.passtival.backend.domain.festival.activity.model.entity.Activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ActivityResponse {

	private final Long id;
	private final String name;
	private final String introduction;
	private final String imagePath;
	private final Integer price;

	public static ActivityResponse from(Activity activity) {
		return ActivityResponse.builder()
			.id(activity.getId())
			.name(activity.getName())
			.introduction(activity.getIntroduction())
			.imagePath(activity.getImagePath())
			.price(activity.getPrice())
			.build();
	}

	public static ActivityResponse of(Activity activity) {
		return ActivityResponse.builder()
			.id(activity.getId())
			.name(activity.getName())
			.introduction(activity.getIntroduction())
			.imagePath(activity.getImagePath())
			.price(activity.getPrice())
			.build();
	}
}
