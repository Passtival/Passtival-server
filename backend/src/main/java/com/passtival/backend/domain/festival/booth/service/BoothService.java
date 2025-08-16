package com.passtival.backend.domain.festival.booth.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.passtival.backend.domain.festival.booth.model.entity.Booth;
import com.passtival.backend.domain.festival.booth.model.response.BoothDetailResponse;
import com.passtival.backend.domain.festival.booth.model.response.BoothResponse;
import com.passtival.backend.domain.festival.booth.repository.BoothRepository;
import com.passtival.backend.domain.festival.menu.model.entity.Menu;
import com.passtival.backend.domain.festival.menu.model.response.MenuResponse;
import com.passtival.backend.domain.festival.performance.model.entity.Performance;
import com.passtival.backend.domain.festival.performance.model.response.PerformanceDetailResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoothService {

	private final BoothRepository boothRepository;

	/**
	 * 모든 부스 목록 조회 (페이징 가능)
	 * @param pageable 페이지 요청 정보
	 * @return Page<Booth>
	 */
	public Page<BoothResponse> getAllBooths(Pageable pageable) throws BaseException {
		Page<Booth> page = boothRepository.findAll(pageable);
		if (page.isEmpty()) {
			throw new BaseException(BaseResponseStatus.BOOTH_NOT_FOUND);
		}
		return page.map(BoothResponse::of);
	}

	// 부스 이름 조회
	public BoothDetailResponse getBoothDetailByName(String name) throws BaseException {
		Optional<Booth> optBooth = boothRepository.findByName(name);
		if (optBooth.isEmpty()) {
			throw new BaseException(BaseResponseStatus.BOOTH_NOT_FOUND);
		}
		return BoothDetailResponse.of(optBooth.get());
	}

	// 부스 이름으로 메뉴 조회
	public List<MenuResponse> getMenusByBoothName(String boothName) throws BaseException {
		Optional<Booth> boothOpt = boothRepository.findByName(boothName);
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
}
