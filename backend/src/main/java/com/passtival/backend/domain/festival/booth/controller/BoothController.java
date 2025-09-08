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
	 * 부스 페이지 단위 전체 목록 조회 (페이징/정렬 지원)
	 */
	@Operation(
		summary = "부스 목록 조회",
		description = "부스 전체 목록을 조회합니다. 페이징 및 정렬 지원.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "부스 목록 조회 성공",
				content = @Content(
					mediaType = "application/json",
					array = @ArraySchema(schema = @Schema(implementation = BoothResponse.class))
				)
			)
		}
	)
	@GetMapping("/booths")
	public BaseResponse<?> getBooths(
		@Parameter(hidden = true) // Swagger에서 Pageable 자동 파라미터는 숨김
		@PageableDefault(size = 5) Pageable pageable
	) {
		Page<BoothResponse> page = boothService.getAllBooths(pageable);
		return BaseResponse.success(page);
	}

	/**
	 * 커서 기반 페이지네이션
	 */
	@Operation(
		summary = "부스 목록 조회 (커서 기반)",
		description = "커서 기반으로 부스를 조회합니다. " +
			"첫 요청은 cursor 없이, 이후 요청은 cursor와 size 지정",
		parameters = {
			@Parameter(
				name = "cursor",
				description = "마지막으로 조회한 부스 ID (없으면 첫 페이지)",
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
				description = "커서 기반 부스 목록 조회 성공",
				content = @Content(
					mediaType = "application/json",
					array = @ArraySchema(schema = @Schema(implementation = BoothResponse.class))
				)
			)
		}
	)
	@GetMapping("booths/cursor")
	public BaseResponse<?> getBoothsCursor(
		@RequestParam(required = false) Long cursor,
		@RequestParam(defaultValue = "5") int size
	) {
		return BaseResponse.success(boothService.getBooths(cursor, size));
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
}
