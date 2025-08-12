package com.passtival.backend.domain.festival.performance.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.festival.performance.model.response.PerformanceResponse;
import com.passtival.backend.domain.festival.performance.model.entity.Performance;
import com.passtival.backend.domain.festival.performance.service.PerformanceService;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/passtival")
public class PerformanceController {

	private final PerformanceService performanceService;

	/**
	 * 공연 목록 조회 (페이징/정렬 지원)
	 * 예: /api/performances?page=0&size=10&sort=date,desc
	 */
	@GetMapping("/performance")
	public BaseResponse<?> getPerformances(
		@PageableDefault(size = 5) Pageable pageable) throws BaseException {
		try {
			Page<Performance> page = performanceService.getAllPerformances(pageable);
			Page<PerformanceResponse> dtoPage = page.map(PerformanceResponse::of);
			return BaseResponse.success(dtoPage);
		} catch (RuntimeException e) {
			return BaseResponse.fail(BaseResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 공연 이름으로 단일 조회
	 */
	@GetMapping("performance/{name}")
	public BaseResponse<PerformanceResponse> getPerformanceByName(
		@PathVariable("performanceName") String performanceName) throws BaseException {
		PerformanceResponse performanceResponse = performanceService.getPerformanceByName(performanceName);
		return BaseResponse.success(performanceResponse);
	}

}
