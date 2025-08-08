package com.passtival.backend.domain.raffle.model.request;

import lombok.Getter;

@Getter
public class UpdateAuthenticationKeyRequest {
	private String newKey;
	private String oldKey;
}
