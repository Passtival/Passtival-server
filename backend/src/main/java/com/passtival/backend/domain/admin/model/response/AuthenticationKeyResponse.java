package com.passtival.backend.domain.admin.model.response;

import lombok.Getter;

@Getter
public class AuthenticationKeyResponse {
	private String authenticationKey; // 인증키

	public AuthenticationKeyResponse(String authenticationKey) {
		this.authenticationKey = authenticationKey;
	}

}
