package com.passtival.backend.domain.raffle.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateAuthenticationKeyRequest {

	@NotBlank(message = "새로운 인증키는 필수입니다.")
	private String newKey;

	@NotBlank(message = "기존 인증키는 필수입니다.")
	private String oldKey;
}
