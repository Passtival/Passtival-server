package com.passtival.backend.domain.matching.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.matching.model.request.MatchingApplicantPatchRequest;
import com.passtival.backend.domain.matching.model.response.MatchingApplicantResponse;
import com.passtival.backend.domain.matching.service.MatchingApplicantService;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.security.model.CustomMemberDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "번호팅 관련 API", description = "번호팅 정보 수집, 정보 조회, 매칭 신청, 결과 조회")
@RequestMapping("/api/matching")
public class MatchingApplicantController {

	private final MatchingApplicantService matchingApplicantService;

	/**
	 * 회원가입 API (소셜 로그인 이후 추가 정보 입력)
	 * @param memberDetails 회원가입 요청 정보
	 * @return 회원가입 결과
	 */

	//소셜 로그인 이후에 추가 정보를 얻는 로직 추가 예정
	@Operation(
		summary = "정보 저장 (추가 정보 입력)",
		description = "Get를 통해 얻은 정보중의 사용자가 추가 정보 혹은 기존의 정보에서 변경하고 싶은 정보를 입력하여,"
			+ " 요청하면 소개팅 정보가 수정된다."
			+ "**인증 토큰이 필요합니다.**\n"
			+ "성별 (필수): db에 성별이 없으면 실패"
			+ "전화번호 허용 형식: \"010-1234-5678\""
			+ "인스타그램 Id 선택 사항",
		security = @SecurityRequirement(name = "jwtAuth"),
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "추가 입력 정보",
			required = true,
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = MatchingApplicantPatchRequest.class),
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
	public BaseResponse<Void> patchProfile(
		@AuthenticationPrincipal CustomMemberDetails memberDetails,
		@Valid @RequestBody MatchingApplicantPatchRequest applicantPatchRequest) {

		// 2. 서비스에 사용자 ID와 DTO 전달
		matchingApplicantService.patchProfile(memberDetails.getMemberId(), applicantPatchRequest);
		return BaseResponse.success(null);
	}

	@Operation(
		summary = "내 소개팅 정보 조회",
		description = "사용자가 번호팅 페이지(응모)에 진입할 때 이 API를 호출 "
			+ "인스타 id, 전화 번호를 기존에 저장된 정보를 불러와서 화면에 채워 넣는다.\n"
			+ "**인증 토큰이 필요합니다.**",
		security = @SecurityRequirement(name = "jwtAuth")
	)
	@GetMapping("/me")
	public BaseResponse<MatchingApplicantResponse> getProfile(
		@AuthenticationPrincipal CustomMemberDetails memberDetails) {

		MatchingApplicantResponse response = matchingApplicantService.getProfile(memberDetails.getMemberId());
		return BaseResponse.success(response);
	}

	@Operation(
		summary = "내 소개팅 정보 생성",
		description = "처음 소셜 로그인을 한다고 해서 "
			+ "소개팅 정보를 저장할 수 있는 것이 아님. 해당 api를 통해 소개팅 회원가입을 해야 함.\n"
			+ "추가정보 불필요 토큰만 필요",
		security = @SecurityRequirement(name = "jwtAuth")
	)
	@PostMapping("/me")
	public BaseResponse<Void> createProfile(
		@AuthenticationPrincipal CustomMemberDetails memberDetails // 1. 현재 로그인한 사용자 정보 가져오기
	) {
		//현재 로그인 되어있는 사용자의 memberID로 MatchingApplicant 생성
		matchingApplicantService.creatProfile(memberDetails.getMemberId());
		return BaseResponse.success(null);
	}
}
