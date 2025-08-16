package com.passtival.backend.domain.matching.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.matching.model.request.MatchingRequest;
import com.passtival.backend.domain.matching.model.response.MatchingResponse;
import com.passtival.backend.domain.matching.service.MatchingService;
import com.passtival.backend.global.auth.model.CustomMemberDetails;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.exception.BaseException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 매칭 관련 API를 처리하는 컨트롤러
 * 프로젝트의 BaseResponse 응답 규격화를 따라 일관된 응답 구조 제공
 */
@Slf4j
@Tag(name = "Matching-API", description = "매칭 관리 API")
@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchingController {

	private final MatchingService matchingService;

	/**
	 * 매칭 신청 API
	 * @param memberDetails 인증된 사용자 정보
	 * @param matchingRequest 매칭 신청 요청 정보 (인스타그램 ID)
	 * @return 매칭 신청 결과
	 * @throws BaseException 매칭 신청 실패 시
	 */
	@Operation(
		summary = "매칭 신청", description = "매칭 신청을 합니다. 인스타그램 ID는 선택사항입니다.",
		security = @SecurityRequirement(name = "jwtAuth"),
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "매칭 신청 요청 정보",
			required = true,
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = MatchingRequest.class),
				examples = @ExampleObject(
					name = "매칭 신청 요청 예시",
					value = """
						{
						  "instagramId": "my_instagram_id"
						}
						"""))))
	@PostMapping("/")
	@PreAuthorize("hasRole('USER')")
	public BaseResponse<Void> applyMatching(@AuthenticationPrincipal CustomMemberDetails memberDetails,
		@Valid @RequestBody MatchingRequest matchingRequest) throws BaseException {
		matchingService.applyMatching(memberDetails.getMemberId(), matchingRequest);
		return BaseResponse.success(null);
	}

	/**
	 * 매칭 결과 조회 API
	 * @param memberDetails 인증된 사용자 정보
	 * @return 매칭 결과 (내 정보 + 파트너 정보)
	 * @throws BaseException 매칭 결과 조회 실패 시
	 */

	@Operation(summary = "매칭 결과 조회", description = "오늘의 매칭 결과를 조회합니다. 매칭 성공 시 내 정보와 파트너 정보를 반환합니다.", security = @SecurityRequirement(name = "jwtAuth"))
	@GetMapping("/")
	@PreAuthorize("hasRole('USER')")
	public BaseResponse<MatchingResponse> getMatchingResult(
		@AuthenticationPrincipal CustomMemberDetails memberDetails) throws BaseException {
		MatchingResponse matchingResponse = matchingService.getMatchingResult(memberDetails.getMemberId());
		return BaseResponse.success(matchingResponse);
	}
}