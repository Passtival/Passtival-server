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
