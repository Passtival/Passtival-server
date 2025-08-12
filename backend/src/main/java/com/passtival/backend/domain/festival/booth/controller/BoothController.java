package com.passtival.backend.domain.festival.booth.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.festival.booth.model.entity.Booth;
import com.passtival.backend.domain.festival.booth.model.response.BoothResponseDTO;
import com.passtival.backend.domain.festival.booth.service.BoothService;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/passtival")
public class BoothController {

	private final BoothService boothService;

	/**
	 * 부스 페이지 단위 전체 목록 조회 (페이징/정렬 지원)
	 * 예: /api/passtival/booth?page=2&pageSize=5&type=동아리&sort=operatingStart,desc&sort=name,asc
	 */
	@GetMapping("/booth")
	public BaseResponse<?> getBooths(
		@PageableDefault(size = 5) Pageable pageable) {
		try {
			Page<BoothResponseDTO> page = boothService.getAllBooths(pageable);
			if (page.isEmpty()) {
				return BaseResponse.fail(BaseResponseStatus.DATA_NULL);
			}
			return BaseResponse.success(page);
		} catch (RuntimeException e) {
			return BaseResponse.fail(BaseResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 부스 이름으로 조회
	 * 예 : /api/passtival/booth/부스이름
	 */
	@GetMapping("/booth/{name}")
	public BaseResponse<BoothResponseDTO> getBoothByName(@PathVariable String name) {
		try {
			BoothResponseDTO booth = boothService.getBoothByName(name);
			return BaseResponse.success(booth);
		} catch (RuntimeException e) {
			return BaseResponse.fail(BaseResponseStatus.NAME_INVALID);
		}
	}
}
