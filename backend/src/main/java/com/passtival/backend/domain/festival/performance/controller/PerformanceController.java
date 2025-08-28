package com.passtival.backend.domain.festival.performance.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.festival.performance.model.entity.Performance;
import com.passtival.backend.domain.festival.performance.model.response.PerformanceDetailResponse;
import com.passtival.backend.domain.festival.performance.model.response.PerformanceResponse;
import com.passtival.backend.domain.festival.performance.service.PerformanceService;
import com.passtival.backend.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/festival")
@Tag(name = "공연 관련 API", description = "공연 조회")
public class PerformanceController {

	private final PerformanceService performanceService;

	/**
	 * 공연 목록 조회 (페이징/정렬 지원)
	 * 예: /performances?page=0&size=10&sort=day,desc
	 * @return 모든 공연 정보 응답
	 */
	@Operation(
		summary = "공연 목록 조회",
		description = "모든 공연을 페이지 단위로 조회합니다. 기본 페이지 크기는 5입니다."
	)
	@GetMapping("/performances")
	public BaseResponse<?> getPerformances(
		@PageableDefault(size = 5) Pageable pageable) {
		Page<Performance> page = performanceService.getAllPerformances(pageable);
		Page<PerformanceResponse> dtoPage = page.map(PerformanceResponse::of);
		return BaseResponse.success(dtoPage);
	}

	/**
	 * 커서기반 페이지네이션
	 * 첫 페이지 요청 (cursor 없음) : GET /performances/cursor
	 * 다음 페이지 요청 (cursor 사용) : GET /performances/cursor?cursor=6&size=5
	 * 사이즈 변경 요청 : GET /performances/cursor?size=10
	 */
	@Operation(
		summary = "공연 목록 조회 (커서 기반)",
		description = "커서 기반으로 공연 목록을 조회합니다. " +
			"첫 요청은 cursor 없이, 이후 요청은 cursor와 size 지정"
	)
	@GetMapping("/performances/cursor")
	public BaseResponse<?> getPerformancesCursor(
		@RequestParam(required = false) Long cursor,
		@RequestParam(defaultValue = "5") int size) {
		return BaseResponse.success(performanceService.getPerformances(cursor, size));
	}

	/**
	 * 공연 id로 단일 조회
	 * @param performanceId 공연 ID
	 * @return 공연 id로 정보 응답
	 */
	@Operation(
		summary = "공연 단일 조회",
		description = "공연 ID로 특정 공연의 상세 정보를 조회합니다.",
		parameters = {
			@Parameter(
				name = "performanceId",
				description = "조회할 공연 ID",
				required = true,
				in = ParameterIn.PATH,
				example = "1"
			)
		}
	)
	@GetMapping("/performances/{performanceId}")
	public BaseResponse<PerformanceDetailResponse> getPerformanceById(
		@PathVariable Long performanceId) {
		PerformanceDetailResponse detail = performanceService.getPerformanceById(performanceId);
		return BaseResponse.success(detail);
	}

}
