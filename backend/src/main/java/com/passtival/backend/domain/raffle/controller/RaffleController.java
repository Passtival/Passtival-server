package com.passtival.backend.domain.raffle.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.member.service.MemberRaffleService;
import com.passtival.backend.domain.raffle.model.response.MemberRaffleProfileResponse;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.security.model.CustomMemberDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/raffle")
@RequiredArgsConstructor
@Tag(name = "응모권 관련 API", description = "상품 조회, 신청자 등록")
public class RaffleController {

	private final MemberRaffleService memberRaffleService;

	// 회원 정보 조회
	@GetMapping("/member")
	@Operation(summary = "회원 응모권 정보 조회",
		description = "로그인한 회원의 응모권 정보를 조회합니다. (응모권 개수, 응모권 사용 내역 등)",
		security = @SecurityRequirement(name = "jwtAuth"))
	public BaseResponse<MemberRaffleProfileResponse> getMemberRaffleProfile(
		@AuthenticationPrincipal CustomMemberDetails member) {

		MemberRaffleProfileResponse response = memberRaffleService.getMemberRaffleProfile(member.getMemberId());
		return BaseResponse.success(response);

	}

}
