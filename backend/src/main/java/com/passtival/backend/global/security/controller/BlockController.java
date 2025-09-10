package com.passtival.backend.global.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

@RestController
public class BlockController {
	@GetMapping({"oauth2/authorization/kakao/**/wlwmanifest.xml", "oauth2/authorization/kakao/**/xmlrpc.php"})
	public void blockBots() {
		throw new BaseException(BaseResponseStatus.NOT_FOUND);
	}
}
