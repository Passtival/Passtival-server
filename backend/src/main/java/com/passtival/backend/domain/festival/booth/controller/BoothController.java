package com.passtival.backend.domain.festival.booth.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.festival.booth.model.response.BoothDetailResponse;
import com.passtival.backend.domain.festival.booth.model.response.BoothResponse;
import com.passtival.backend.domain.festival.booth.service.BoothService;
import com.passtival.backend.domain.festival.menu.model.response.MenuResponse;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/festival")
public class BoothController {

	private final BoothService boothService;

	/**
	 * 부스 페이지 단위 전체 목록 조회 (페이징/정렬 지원)
	 * 예: /api/festival/booth?page=2&pageSize=5&type=동아리&sort=operatingStart,desc&sort=name,asc
	 * @return BoothResponse
	 */
	@GetMapping("/booth")
	public BaseResponse<?> getBooths(
		@PageableDefault(size = 5) Pageable pageable) throws BaseException {
		Page<BoothResponse> page = boothService.getAllBooths(pageable);
		return BaseResponse.success(page);
	}

	/**
	 * 부스 이름으로 조회
	 * @param {name} 부스 이름 요청 정보
	 * @return BoothResponse
	 */
	@GetMapping("/booth/{boothName}")
	public BaseResponse<BoothDetailResponse> getBoothDetail(@PathVariable String boothName) throws BaseException {
		BoothDetailResponse boothDetail = boothService.getBoothDetailByName(boothName);
		return BaseResponse.success(boothDetail);
	}

	/**
	 * 부스 이름으로 해당 부스의 메뉴 조회
	 */
	@GetMapping("/{boothName}/menus")
	public BaseResponse<List<MenuResponse>> getMenusByBoothName(@PathVariable String boothName) throws BaseException {
		List<MenuResponse> menus = boothService.getMenusByBoothName(boothName);
		return BaseResponse.success(menus);
	}
}
