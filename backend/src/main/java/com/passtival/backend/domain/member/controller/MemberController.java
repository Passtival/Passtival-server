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
		return new RedirectView("/oauth2/authorization/kakao"); // localhost:8080/oauth2/authorization/kakao
	}

	// 회원 레벨업 신청
	@PatchMapping("/level-up")
	public BaseResponse<Void> levelUp(
		@AuthenticationPrincipal CustomMemberDetails member,
		@RequestBody LevelUpRequest request
	) {

		Long memberId = member.getMemberId();

		memberService.levelUp(memberId, request);

		return BaseResponse.success(null);
	}

}
