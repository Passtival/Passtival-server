package com.passtival.backend.domain.lostfound.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminLoginRequest {

	@NotBlank(message = "관리자 id를 입력하세요")
	private String loginId;

	@NotBlank(message = "관리자 인증키를 입력하세요")
	private String authKey;
}