package com.passtival.backend.domain.raffle.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateAuthenticationKeyRequest {

	@NotBlank
	private String newKey;
	@NotBlank
	private String oldKey;
}
