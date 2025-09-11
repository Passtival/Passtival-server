package com.passtival.backend.global.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import io.swagger.v3.oas.annotations.Hidden;

@RestController
@Hidden

public class BlockController {

	/**
	 * 토이 프로젝트 박준선의 블로그 조회수 올리기 프로젝트
	 */
	@GetMapping({"oauth2/authorization/kakao/**/wlwmanifest.xml",
		"oauth2/authorization/kakao/**/xmlrpc.php",
		"oauth2/authorization/kakao/**/sitemap.xml.",
		"oauth2/authorization/kakao/**/robots.txt"
	})
	public RedirectView blockBots() {
		return new RedirectView("https://mydcaf.tistory.com/");
	}
}