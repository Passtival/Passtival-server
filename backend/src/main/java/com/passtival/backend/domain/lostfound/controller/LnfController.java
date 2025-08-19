package com.passtival.backend.domain.lostfound.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.lostfound.model.request.FoundItemRequest;
import com.passtival.backend.domain.lostfound.service.LnfService;
import com.passtival.backend.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/found-items")
@Tag(name = "Lost and Found API", description = "분실물 관리 API")
public class LnfController {

	private final LnfService lnfService;

	@Operation(
		summary = "이미지 업로드 URL 조회",
		description = "클라이언트가 S3에 직접 이미지를 업로드하기 위한 Presigned URL을 생성합니다.",
		parameters = {
			@Parameter(
				name = "fileName",
				description = "업로드할 이미지 파일명 (확장자 포함)",
				required = true,
				in = ParameterIn.QUERY,
				example = "lost-wallet.jpg"
			)
		}
	)
	@GetMapping("/upload-url")
	public BaseResponse<String> getUploadUrl(
		@RequestParam
		@NotBlank(message = "파일명은 필수입니다.")
		@Pattern(regexp = "^[a-zA-Z0-9._-]+\\.(jpg|jpeg|png|gif)$",
			message = "유효한 이미지 파일명이어야 합니다.")
		String fileName) {
		String uploadUrl = lnfService.getUploadUrl(fileName);
		return BaseResponse.success(uploadUrl);
	}

	@Operation(
		summary = "분실물 등록",
		description = "습득한 분실물의 정보를 등록합니다. 이미지는 사전에 업로드하고 해당 URL을 포함해야 합니다.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "분실물 등록 요청 정보",
			required = true,
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = FoundItemRequest.class),
				examples = @ExampleObject(
					name = "분실물 등록 요청 예시",
					value = """
						{
						  "title": "검은색 지갑",
						  "area": "메인스테이지 앞",
						  "foundDateTime": "2024-01-15T14:30:00",
						  "imagePath": "https://bucket-name.s3.amazonaws.com/images/found/wallet-image.jpg"
						}
						"""
				)
			)
		)
	)
	@PostMapping
	public BaseResponse<Void> createFoundItem(@Valid @RequestBody FoundItemRequest request) {
		lnfService.createFoundItem(request);
		return BaseResponse.success(null);
	}

}
