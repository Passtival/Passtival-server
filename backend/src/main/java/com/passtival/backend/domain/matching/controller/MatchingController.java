package com.passtival.backend.domain.matching.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.matching.model.response.MatchingResponse;
import com.passtival.backend.domain.matching.service.MatchingService;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.security.model.CustomMemberDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "번호팅 관련 API", description = "번호팅 정보 수집, 정보 조회, 매칭 신청, 결과 조회")
@RequestMapping("/api/matching")
public class MatchingController {

	private final MatchingService matchingService;

	/**
	 * 매칭 신청 API
	 * @param memberDetails 인증된 사용자 정보
	 * @return 매칭 신청 결과
	 */
	@Operation(
		summary = "매칭 신청",
		description = "매칭 신청을 합니다. 별도의 추가 정보 없이 신청만 처리됩니다.",
		security = @SecurityRequirement(name = "jwtAuth")
		// requestBody 관련 어노테이션 모두 제거
	)
	@PostMapping()
	@PreAuthorize("hasRole('USER')")
	public BaseResponse<Void> applyMatching(@AuthenticationPrincipal CustomMemberDetails memberDetails) {
		matchingService.applyMatching(memberDetails.getMemberId());
		return BaseResponse.success(null);
	}

	/**
	 * 매칭 결과 조회 API
	 * @param memberDetails 인증된 사용자 정보
	 * @return 매칭 결과 (내 정보 + 파트너 정보)
	 */

	@Operation(summary = "매칭 결과 조회",
		description = "오늘의 매칭 결과를 조회합니다. "
			+ "매칭 성공 시 내 정보와 파트너 정보를 반환합니다."
			+ "실패하면 오늘 소개팅 정보가 없다고 나옴",
		security = @SecurityRequirement(name = "jwtAuth"))
	@GetMapping("/result")
	@PreAuthorize("hasRole('USER')")
	public BaseResponse<MatchingResponse> getMatchingResult(
		@AuthenticationPrincipal CustomMemberDetails memberDetails) {
		MatchingResponse matchingResponse = matchingService.getMatchingResult(memberDetails.getMemberId());
		return BaseResponse.success(matchingResponse);
	}
}