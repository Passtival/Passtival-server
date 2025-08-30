package com.passtival.backend.domain.lostfound.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.lostfound.model.response.FoundItemResponse;
import com.passtival.backend.domain.lostfound.service.LnfService;
import com.passtival.backend.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/found-items")
@Tag(name = "분실물 관련 API", description = "분실물 등록, 조회, 삭제 API")
public class LnfController {

	private final LnfService lnfService;

	@Operation(
		summary = "이미지 업로드 URL(PreSignedURL) 조회",
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
		@NotBlank(message = "파일명은 한글, 영문, 숫자, ., _, - 문자로 이루어져야 하며, 공백일 수 없으며, jpg, jpeg, png, gif, heic, webp 확장자를 포함해야 합니다.")
		@Pattern(regexp = "^[\\w가-힣._-]+\\.(jpg|jpeg|png|gif|heic|webp)$",
			message = "유효한 이미지 파일명이어야 합니다.")
		String fileName) {
		String uploadUrl = lnfService.getUploadUrl(fileName);
		return BaseResponse.success(uploadUrl);
	}

	@Operation(
		summary = "분실물 상세 조회",
		description = "ID로 등록된 분실물의 상세 정보를 조회합니다.",
		parameters = {
			@Parameter(
				name = "id",
				description = "조회할 분실물의 ID",
				required = true,
				in = ParameterIn.PATH,
				example = "1"
			)
		}
	)
	@GetMapping("/{id}")
	public BaseResponse<FoundItemResponse> getFoundItemById(@PathVariable Long id) {
		FoundItemResponse response = lnfService.getFoundItemById(id);
		return BaseResponse.success(response);
	}

	@Operation(
		summary = "모든 분실물 조회",
		description = "등록된 모든 분실물의 정보를 조회합니다."
	)
	@GetMapping
	public BaseResponse<List<FoundItemResponse>> getAllFoundItems() {
		List<FoundItemResponse> responses = lnfService.getAllFoundItems();
		return BaseResponse.success(responses);
	}

}
