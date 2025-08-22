package com.passtival.backend.domain.authentication.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.passtival.backend.domain.authentication.service.AuthenticationService;
import com.passtival.backend.domain.raffle.model.request.UpdateAuthenticationKeyRequest;
import com.passtival.backend.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/authentication")
public class AuthenticationController {

	private final AuthenticationService authenticationService;

	@Operation(
		summary = "인증키 변경",
		description = "기존 인증키를 검증하고 새로운 인증키로 변경합니다.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "인증키 변경 요청 정보",
			required = true,
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = UpdateAuthenticationKeyRequest.class),
				examples = @ExampleObject(
					name = "인증키 변경 요청",
					value = """
						{
						  "oldKey": "1234",
						  "newKey": "5678"
						}
						"""
				)
			)
		)
	)
	@PutMapping()
	public BaseResponse<Void> updateAuthenticationKey(@Valid @RequestBody UpdateAuthenticationKeyRequest request) {
		authenticationService.updateAuthenticationKey(request.getNewKey(), request.getOldKey());
		return BaseResponse.success(null);
	}

}
