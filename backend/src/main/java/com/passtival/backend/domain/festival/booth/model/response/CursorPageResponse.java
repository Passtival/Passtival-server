package com.passtival.backend.domain.festival.booth.model.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CursorPageResponse<T> {
	private List<T> content;

	private Integer nextTypeOrder;
	private String nextType;
	private String nextName;
	private Long nextId;
}
