package com.passtival.backend.domain.raffle.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.raffle.model.response.PrizeResponse;
import com.passtival.backend.domain.raffle.service.PrizeService;
import com.passtival.backend.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "응모권 관련 API", description = "상품 조회, 신청자 등록")
@RequestMapping("/api/raffle/prizes")
public class PrizeController {

	private final PrizeService prizeService;

	@Operation(
		summary = "상품 목록 조회",
		description = "등록된 상품의 목록을 조회합니다."
	)
	@GetMapping()
	public BaseResponse<List<PrizeResponse>> getPrizes() {
		List<PrizeResponse> responses = prizeService.getAllPrizes();
		return BaseResponse.success(responses);
	}

	@Operation(
		summary = "상품 조회",
		description = "상품 ID로 특정 상품의 정보를 조회합니다.",
		parameters = {
			@Parameter(
				name = "prizeId",
				description = "조회할 상품의 ID",
				required = true,
				in = ParameterIn.PATH,
				example = "1"
			)
		}
	)
	@GetMapping("/{prizeId}")
	public BaseResponse<PrizeResponse> getPrizeById(@PathVariable("prizeId") Long prizeId) {
		PrizeResponse response = prizeService.getPrizeById(prizeId);
		return BaseResponse.success(response);
	}
}
