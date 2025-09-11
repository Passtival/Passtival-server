package com.passtival.backend.domain.member.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.passtival.backend.domain.member.model.entity.request.LevelUpRequest;
import com.passtival.backend.domain.member.service.MemberService;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.security.model.CustomMemberDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Tag(name = "회원 관련 API", description = "카카오 로그인")
public class MemberController {

	private final MemberService memberService;

	@Operation(
		summary = "사용자 로그인 - 소개팅 소셜로그인",
		description = "GET 요청을 받고 kakao 로그인 페이지를 리다이렉트합니다."
	)
	@GetMapping("/login/kakao")
	public RedirectView redirectLoginKakao() {
		return new RedirectView(
			"https://passtival.co.kr/oauth2/authorization/kakao"); // localhost:8080/oauth2/authorization/kakao
	}

	// 회원 레벨업 신청
	@PatchMapping("/level-up")
	@Operation(
		summary = "사용자 레벨업 신청",
		description = "부스를 돌고 학생회를 찾아가서 본인에 맞는 레벨의 인증키를 발급 받고"
			+ "올바른 인증키와 학번, 이름과 본인이 될 레벨을 함께 입력하면 성공"
			+ "인증키 레벨과 요청하는 레벨이 다르면 실패",
		security = @SecurityRequirement(name = "jwtAuth")
	)
	public BaseResponse<Void> levelUp(
		@AuthenticationPrincipal CustomMemberDetails member,
		@RequestBody LevelUpRequest request
	) {

		Long memberId = member.getMemberId();

		memberService.levelUp(memberId, request);

		return BaseResponse.success(null);
	}

}
