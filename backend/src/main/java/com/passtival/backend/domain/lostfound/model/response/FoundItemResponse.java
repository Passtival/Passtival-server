package com.passtival.backend.domain.lostfound.model.response;

import java.time.LocalDateTime;

import com.passtival.backend.domain.lostfound.model.entity.FoundItem;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FoundItemResponse {
	private Long id;
	private String title;
	private String area;
	private LocalDateTime foundDateTime; // 습득 일시
	private String imagePath; // 이미지 경로

	public static FoundItemResponse of(FoundItem foundItem) {
		return FoundItemResponse.builder()
			.id(foundItem.getId())
			.title(foundItem.getTitle())
			.area(foundItem.getArea())
			.foundDateTime(foundItem.getFoundDateTime())
			.imagePath(foundItem.getImagePath())
			.build();
	}
}
