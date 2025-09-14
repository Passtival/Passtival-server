package com.passtival.backend.domain.festival.booth.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.passtival.backend.domain.festival.activity.model.entity.Activity;
import com.passtival.backend.domain.festival.booth.model.entity.Booth;
import com.passtival.backend.domain.festival.booth.model.response.ActivityResponse;
import com.passtival.backend.domain.festival.booth.model.response.BoothDetailResponse;
import com.passtival.backend.domain.festival.booth.model.response.BoothResponse;
import com.passtival.backend.domain.festival.booth.model.response.CursorPageResponse;
import com.passtival.backend.domain.festival.booth.repository.BoothRepository;
import com.passtival.backend.domain.festival.menu.model.entity.Menu;
import com.passtival.backend.domain.festival.menu.model.response.MenuResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoothService {

	private final BoothRepository boothRepository;

	/**
	 * 커서기반 페이지네이션
	 */
	public CursorPageResponse<BoothResponse> getBooths(
		Integer lastTypeOrder, String lastType, String lastName, Long lastId, int size) {

		Pageable pageable = PageRequest.of(0, size);
		List<Booth> booths = boothRepository.findPageByCursor(lastTypeOrder, lastType, lastName, lastId, pageable);

		if (booths.isEmpty()) {
			throw new BaseException(BaseResponseStatus.BOOTH_NOT_FOUND);
		}

		Booth last = booths.get(booths.size() - 1);

		Integer nextTypeOrder = typeToOrder(last.getType());
		String nextType = last.getType();
		String nextName = last.getName();
		Long nextId = last.getId();

		return new CursorPageResponse<>(
			booths.stream().map(BoothResponse::of).toList(),
			nextTypeOrder, nextType, nextName, nextId
		);
	}

	private int typeToOrder(String type) {
		return switch (type) {
			case "학내부스" -> 1;
			case "체험"   -> 2;
			case "푸드존" -> 3;
			case "의료지원" -> 4;
			default -> 5;
		};
	}


	// 부스 ID 조회
	public BoothDetailResponse getBoothDetailById(Long boothId) {
		Optional<Booth> optBooth = boothRepository.findById(boothId);
		if (optBooth.isEmpty()) {
			throw new BaseException(BaseResponseStatus.BOOTH_NOT_FOUND);
		}
		return BoothDetailResponse.of(optBooth.get());
	}

	// 부스 ID로 메뉴 조회
	public List<MenuResponse> getMenusByBoothId(Long boothId) {
		Optional<Booth> boothOpt = boothRepository.findById(boothId);
		if (boothOpt.isEmpty()) {
			throw new BaseException(BaseResponseStatus.BOOTH_NOT_FOUND);
		}

		List<Menu> menus = boothOpt.get().getMenus();
		if (menus == null) {
			menus = Collections.emptyList();
		}

		return menus.stream()
			.map(MenuResponse::from)
			.collect(Collectors.toList());
	}

	/**
	 * 부스 ID로 해당 부스의 체험활동 조회
	 */
	public List<ActivityResponse> getActivitiesByBoothId(Long boothId) {
		Optional<Booth> boothOpt = boothRepository.findById(boothId);
		if (boothOpt.isEmpty()) {
			throw new BaseException(BaseResponseStatus.BOOTH_NOT_FOUND);
		}

		List<Activity> activities = boothOpt.get().getActivities();
		if (activities == null) {
			activities = Collections.emptyList();
		}

		return activities.stream()
			.map(ActivityResponse::from)
			.collect(Collectors.toList());
	}

}
