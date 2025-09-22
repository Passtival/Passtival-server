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
	public CursorPageResponse<BoothResponse> getBooths(Long cursorId, int size) {
		Pageable pageable = PageRequest.ofSize(size); // offset=0 대신 ofSize만 사용
		List<Booth> booths = boothRepository.findPageByCursor(cursorId, pageable);

		if (booths.isEmpty()) {
			throw new BaseException(BaseResponseStatus.BOOTH_NOT_FOUND); // 부스 없음 예외
		}

		Long nextCursor = booths.get(booths.size() - 1).getLocation_id();

		// 마지막 페이지 여부 추가
		boolean isLast = booths.size() < size;

		return new CursorPageResponse<>(
			booths.stream().map(BoothResponse::of).toList(),
			nextCursor,
			isLast
		);
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
