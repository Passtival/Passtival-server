package com.passtival.backend.domain.matching.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.matching.model.request.MemberPatchRequest;
import com.passtival.backend.domain.matching.service.MemberService;
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

@Slf4j
@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
@Tag(name = "Member-API", description = "회원 관리 API")
public class MemberController {

	private final MemberService memberService;

	/**
	 * 회원가입 API (소셜 로그인 이후 추가 정보 입력)
	 * @param memberDetails 회원가입 요청 정보
	 * @return 회원가입 결과
	 * @throws BaseException 회원가입 실패 시
	 */

	//소셜 로그인 이후에 추가 정보를 얻는 로직 추가 예정
	@Operation(
		summary = "온보딩 (추가 정보 입력)",
		description = "소셜 로그인으로 가입된 사용자가 추가 정보(성별, 전화번호)를 입력 **인증 토큰이 필요합니다.**\n"
			+ "성별 (필수): db에 성별이 없으면 실패"
			+ "전화번호 허용 형식: \"010-1234-5678\""
			+ "인스타그램 Id 선택 사항",
		security = @SecurityRequirement(name = "jwtAuth"),
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "추가 입력 정보",
			required = true,
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = MemberPatchRequest.class),
				examples = @ExampleObject(
					name = "온보딩 요청 예시",
					value = """
						{
						  "gender": "MALE",
						  "phoneNumber": "010-1234-5678",
						  "instagramId": "one_112"
						}
						"""
				)
			)
		)
	)
	@PatchMapping("/me")
	public BaseResponse<Void> updateProfile(
		@AuthenticationPrincipal CustomMemberDetails memberDetails, // 1. 현재 로그인한 사용자 정보 가져오기
		@Valid @RequestBody MemberPatchRequest memberPatchRequest) throws BaseException {

		// 2. 서비스에 사용자 ID와 DTO 전달
		memberService.patchProfile(memberDetails.getMemberId(), memberPatchRequest);
		return BaseResponse.success(null);
	}

	//updateProfile 구현 후 구현 예정
	// @GetMapping("/me")
	// public BaseResponse<MemberResponse> getProfile() throws BaseException {
	// 	MemberResponse response;
	// 	return BaseResponse.success(response);
	// }
}
