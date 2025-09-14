package com.passtival.backend.domain.festival.booth.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.festival.booth.model.response.ActivityResponse;
import com.passtival.backend.domain.festival.booth.model.response.BoothDetailResponse;
import com.passtival.backend.domain.festival.booth.model.response.BoothResponse;
import com.passtival.backend.domain.festival.booth.service.BoothService;
import com.passtival.backend.domain.festival.menu.model.response.MenuResponse;
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
@Tag(name = "부스 관련 API", description = "부스, 메뉴 조회")
public class BoothController {

	private final BoothService boothService;

	/**
	 * 커서 기반 페이지네이션
	 */
	@Operation(
		summary = "부스 목록 조회 (커서 기반)",
		description = "첫 요청은 커서 없이 호출하고, 이후 요청은 응답의 nextCursor 정보를 쿼리파라미터로 넘기세요",
		parameters = {
			@Parameter(name = "lastTypeOrder", example = "2"),
			@Parameter(name = "lastType", example = "체험"),
			@Parameter(name = "lastName", example = "VR게임"),
			@Parameter(name = "lastId", example = "15"),
			@Parameter(name = "size", example = "5")
		}
	)
	@GetMapping("booths/cursor")
	public BaseResponse<?> getBoothsCursor(
		@RequestParam(required = false) Integer lastTypeOrder,
		@RequestParam(required = false) String lastType,
		@RequestParam(required = false) String lastName,
		@RequestParam(required = false) Long lastId,
		@RequestParam(defaultValue = "5") int size
	) {
		return BaseResponse.success(
			boothService.getBooths(lastTypeOrder, lastType, lastName, lastId, size)
		);
	}

	/**
	 * 부스 id로 조회
	 */
	@Operation(
		summary = "부스 단일 조회",
		description = "부스 ID로 특정 부스 정보를 조회합니다.",
		parameters = {
			@Parameter(
				name = "boothId",
				description = "조회할 부스의 ID",
				required = true,
				in = ParameterIn.PATH,
				example = "1"
			)
		},
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "부스 단일 조회 성공",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = BoothDetailResponse.class)
				)
			)
		}
	)
	@GetMapping("/booths/{boothId}")
	public BaseResponse<BoothDetailResponse> getBoothDetail(@PathVariable Long boothId) {
		BoothDetailResponse boothDetail = boothService.getBoothDetailById(boothId);
		return BaseResponse.success(boothDetail);
	}

	/**
	 * 부스 ID로 해당 부스의 메뉴 조회
	 */
	@Operation(
		summary = "부스 ID로 메뉴 조회",
		description = "부스 ID로 특정 부스의 메뉴들을 조회합니다.",
		parameters = {
			@Parameter(
				name = "boothId",
				description = "조회할 부스의 ID",
				required = true,
				in = ParameterIn.PATH,
				example = "1"
			)
		},
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "부스 메뉴 조회 성공",
				content = @Content(
					mediaType = "application/json",
					array = @ArraySchema(schema = @Schema(implementation = MenuResponse.class))
				)
			)
		}
	)
	@GetMapping("/{boothId}/menus")
	public BaseResponse<List<MenuResponse>> getMenusByBoothId(@PathVariable Long boothId) {
		List<MenuResponse> menus = boothService.getMenusByBoothId(boothId);
		return BaseResponse.success(menus);
	}

	@Operation(
		summary = "부스 ID로 체험활동 조회",
		description = "부스 ID로 특정 부스의 체험활동들을 조회합니다.",
		parameters = {
			@Parameter(
				name = "boothId",
				description = "조회할 부스의 ID",
				required = true,
				in = ParameterIn.PATH,
				example = "1"
			)
		},
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "부스 체험활동 조회 성공",
				content = @Content(
					mediaType = "application/json",
					array = @ArraySchema(schema = @Schema(implementation = ActivityResponse.class))
				)
			)
		}
	)
	@GetMapping("/{boothId}/activities")
	public BaseResponse<List<ActivityResponse>> getActivitiesByBoothId(@PathVariable Long boothId) {
		List<ActivityResponse> activities = boothService.getActivitiesByBoothId(boothId);
		return BaseResponse.success(activities);
	}
}
