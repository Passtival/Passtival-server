package com.passtival.backend.domain.lostfound.model.request;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FoundItemRequest {

	private String title;
	private String area;
	private LocalDateTime foundDateTime;
	private String imagePath;

}
