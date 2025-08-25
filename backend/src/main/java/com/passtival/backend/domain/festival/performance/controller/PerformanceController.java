package com.passtival.backend.domain.festival.performance.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.festival.performance.model.response.PerformanceDetailResponse;
import com.passtival.backend.domain.festival.performance.model.response.PerformanceResponse;
import com.passtival.backend.domain.festival.performance.model.entity.Performance;
import com.passtival.backend.domain.festival.performance.service.PerformanceService;
import com.passtival.backend.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/festival")
@Tag(name = "Performance-API", description = "공연 목록 조회 API")
public class PerformanceController {

	private final PerformanceService performanceService;

	/**
	 * 공연 목록 조회 (페이징/정렬 지원)
	 * 예: /api/performances?page=0&size=10&sort=date,desc
	 * @return 모든 공연 정보 응답
	 */
	@GetMapping("/performance")
	public BaseResponse<?> getPerformances(
		@PageableDefault(size = 5) Pageable pageable) {
		Page<Performance> page = performanceService.getAllPerformances(pageable);
		Page<PerformanceResponse> dtoPage = page.map(PerformanceResponse::of);
		return BaseResponse.success(dtoPage);
	}

	/**
	 * 커서기반 페이지네이션
	 * 첫 페이지 요청 (cursor 없음) : GET /performance/cursor
	 * 다음 페이지 요청 (cursor 사용) : GET /performance/cursor?cursor=6&size=5
	 * 사이즈 변경 요청 : GET /performance/cursor?size=10
	 */
	@GetMapping("/performance/cursor")
	public BaseResponse<?> getPerformancesCursor(
		@RequestParam(required = false) Long cursor,
		@RequestParam(defaultValue = "5") int size) {
		return BaseResponse.success(performanceService.getPerformances(cursor, size));
	}

	/**
	 * 공연 id로 단일 조회
	 * @param performanceId 공연 이름
	 * @return 공연 id로 정보 응답
	 */
	@GetMapping("/performance/{performanceId}")
	public BaseResponse<PerformanceDetailResponse> getPerformanceById(
		@PathVariable Long performanceId) {
		PerformanceDetailResponse detail = performanceService.getPerformanceById(performanceId);
		return BaseResponse.success(detail);
	}



}
