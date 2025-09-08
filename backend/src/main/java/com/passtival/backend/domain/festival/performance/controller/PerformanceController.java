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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
	 */
	@Operation(
		summary = "공연 목록 조회",
		description = "모든 공연을 페이지 단위로 조회합니다. 기본 페이지 크기는 5입니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "공연 목록 조회 성공",
				content = @Content(
					mediaType = "application/json",
					array = @ArraySchema(schema = @Schema(implementation = PerformanceResponse.class))
				)
			)
		}
	)
	@GetMapping("/performances")
	public BaseResponse<?> getPerformances(
		@Parameter(hidden = true) // Swagger에 pageable 자동 파라미터 안 보이게
		@PageableDefault(size = 5) Pageable pageable
	) {
		Page<Performance> page = performanceService.getAllPerformances(pageable);
		Page<PerformanceResponse> dtoPage = page.map(PerformanceResponse::of);
		return BaseResponse.success(dtoPage);
	}

	/**
	 * 커서 기반 페이지네이션
	 * 첫 요청: GET /performances/cursor
	 * 이후 요청: GET /performances/cursor?cursor=6&size=5
	 */
	@Operation(
		summary = "공연 목록 조회 (커서 기반)",
		description = "커서 기반으로 공연 목록을 조회합니다.",
		parameters = {
			@Parameter(
				name = "cursor",
				description = "마지막으로 조회한 공연 ID (없으면 첫 페이지)",
				example = "6",
				in = ParameterIn.QUERY
			),
			@Parameter(
				name = "size",
				description = "한 페이지 크기",
				example = "5",
				in = ParameterIn.QUERY
			)
		},
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "커서 기반 공연 목록 조회 성공",
				content = @Content(
					mediaType = "application/json",
					array = @ArraySchema(schema = @Schema(implementation = PerformanceResponse.class))
				)
			)
		}
	)
	@GetMapping("/performances/cursor")
	public BaseResponse<?> getPerformancesCursor(
		@RequestParam(required = false) Long cursor,
		@RequestParam(defaultValue = "5") int size
	) {
		return BaseResponse.success(performanceService.getPerformances(cursor, size));
	}

	/**
	 * 공연 id로 단일 조회
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
		},
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "공연 단일 조회 성공",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = PerformanceDetailResponse.class)
				)
			)
		}
	)
	@GetMapping("/performances/{performanceId}")
	public BaseResponse<PerformanceDetailResponse> getPerformanceById(
		@PathVariable Long performanceId
	) {
		PerformanceDetailResponse detail = performanceService.getPerformanceById(performanceId);
		return BaseResponse.success(detail);
	}

	@Operation(
		summary = "공연 시간순 조회",
		description = "현재 시간과 가장 가까운 공연부터 순서대로 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "공연 시간순 조회 성공",
				content = @Content(
					mediaType = "application/json",
					array = @ArraySchema(schema = @Schema(implementation = PerformanceResponse.class))
				)
			)
		}
	)
	@GetMapping("/performances/closest")
	public BaseResponse<?> getPerformancesByClosestTime() {
		return BaseResponse.success(performanceService.getPerformancesByClosestTime());
	}

}
