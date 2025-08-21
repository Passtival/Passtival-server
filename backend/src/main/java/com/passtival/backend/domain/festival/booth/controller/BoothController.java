package com.passtival.backend.domain.festival.booth.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.festival.booth.model.response.BoothDetailResponse;
import com.passtival.backend.domain.festival.booth.model.response.BoothResponse;
import com.passtival.backend.domain.festival.booth.service.BoothService;
import com.passtival.backend.domain.festival.menu.model.response.MenuResponse;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/festival")
@Tag(name = "Booth-API", description = "부스 조회 API")
public class BoothController {

	private final BoothService boothService;

	/**
	 * 부스 페이지 단위 전체 목록 조회 (페이징/정렬 지원)
	 * 예: /api/festival/booth?page=2&pageSize=5&type=동아리&sort=operatingStart,desc&sort=name,asc
	 * @return BoothResponse
	 */
	@Operation(
		summary = "부스 목록 조회",
		description = "부스전체 목록을 조회합니다."
	)
	@GetMapping("/booth")
	public BaseResponse<?> getBooths(
		@PageableDefault(size = 5) Pageable pageable) {
		Page<BoothResponse> page = boothService.getAllBooths(pageable);
		return BaseResponse.success(page);
	}

	/**
	 * 커서기반 페이지네이션
	 * 첫 페이지 요청 (cursor 없음) : GET /booth/cursor
	 * 다음 페이지 요청 (cursor 사용) : GET /booth/cursor?cursor=6&size=5
	 * 사이즈 변경 요청 : GET /booth/cursor?size=10
	 */
	@GetMapping("booth/cursor")
	public BaseResponse<?> getBoothsCursor(
		@RequestParam(required = false) Long cursor,
		@RequestParam(defaultValue = "5") int size) {
		return BaseResponse.success(boothService.getBooths(cursor, size));
	}


	/**
	 * 부스 이름으로 조회
	 * @param {name} 부스 이름 요청 정보
	 * @return BoothResponse
	 */
	@Operation(
		summary = "부스 이름으로 조회",
		description = "상품 boothName으로 특정 부스의 정보를 조회합니다.",
		parameters = {
			@Parameter(
				name = "{name}",
				description = "조회할 부스의 이름",
				required = true,
				in = ParameterIn.PATH,
				example = "신석기"
			)
		}
	)

	@GetMapping("/booth/{boothName}")
	public BaseResponse<BoothDetailResponse> getBoothDetail(@PathVariable String boothName) {
		BoothDetailResponse boothDetail = boothService.getBoothDetailByName(boothName);
		return BaseResponse.success(boothDetail);
	}

	/**
	 * 부스 이름으로 해당 부스의 메뉴 조회
	 */
	@Operation(
		summary = "부스 이름으로 해당 부스의 메뉴 조회",
		description = "부스 이름으로 특정 부스의 메뉴들을 조회합니다.",
		parameters = {
			@Parameter(
				name = "{boothName}",
				description = "조회할 부스의 name",
				required = true,
				in = ParameterIn.PATH,
				example = "신석기"
			)
		}
	)

	@GetMapping("/{boothName}/menus")
	public BaseResponse<List<MenuResponse>> getMenusByBoothName(@PathVariable String boothName) {
		List<MenuResponse> menus = boothService.getMenusByBoothName(boothName);
		return BaseResponse.success(menus);
	}
}
