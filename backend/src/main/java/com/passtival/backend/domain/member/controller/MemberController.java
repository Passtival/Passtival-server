package com.passtival.backend.domain.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Tag(name = "로그인 관련 API", description = "번호팅 카카오 로그인")
public class MemberController {
	@Operation(
		summary = "사용자 로그인 - 소개팅 소셜로그인",
		description = "GET 요청을 받고 kakao 로그인 페이지를 리다이렉트합니다."
	)
	@GetMapping("/login/kakao")
	public RedirectView redirectLoginKakao() {
		return new RedirectView("/oauth2/authorization/kakao");
	}
}
