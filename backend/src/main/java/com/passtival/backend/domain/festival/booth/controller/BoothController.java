package com.passtival.backend.domain.festival.booth.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.festival.booth.model.response.BoothResponse;
import com.passtival.backend.domain.festival.booth.service.BoothService;
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
	@GetMapping("/booth/{name}")
	public BaseResponse<BoothResponse> getBoothByName(@PathVariable String name) {
		BoothResponse booth = boothService.getBoothByName(name);
		return BaseResponse.success(booth);
	}
}
