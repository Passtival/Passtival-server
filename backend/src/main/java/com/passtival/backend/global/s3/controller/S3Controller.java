package com.passtival.backend.global.s3.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.festival.booth.service.BoothService;
import com.passtival.backend.domain.festival.performance.service.PerformanceService;
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
@RequestMapping("/api/s3")
@Tag(name = "분실물 관련 API", description = "분실물 등록, 조회, 삭제 API")
public class S3Controller {

	private final LnfService lnfService;
	private final BoothService boothService;
	private final PerformanceService performanceService;

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
	@GetMapping("/lnfService/upload-url")
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
		summary = "부스 이미지 업로드 URL(PreSignedURL) 조회",
		description = "부스/메뉴 이미지 파일을 S3에 직접 업로드하기 위한 Presigned URL을 생성합니다.",
		parameters = {
			@Parameter(
				name = "fileName",
				description = "업로드할 이미지 파일명 (확장자 포함)",
				required = true,
				in = ParameterIn.QUERY,
				example = "booth-banner.jpg"
			)
		}
	)
	@GetMapping("/booths/upload-url")
	public BaseResponse<String> getBoothUploadUrl(
		@RequestParam
		@NotBlank(message = "파일명은 한글, 영문, 숫자, ., _, - 문자로 이루어져야 하며, 공백일 수 없으며, jpg, jpeg, png, gif, heic, webp 확장자를 포함해야 합니다.")
		@Pattern(
			regexp = "^[\\w가-힣._-]+\\.(jpg|jpeg|png|gif|heic|webp)$",
			message = "유효한 이미지 파일명이어야 합니다."
		)
		String fileName
	) {
		String uploadUrl = boothService.getUploadUrl(fileName);
		return BaseResponse.success(uploadUrl);
	}

	@Operation(
		summary = "공연 이미지 업로드 URL(PreSignedURL) 조회",
		description = "공연 포스터/이미지를 S3에 직접 업로드하기 위한 Presigned URL을 생성합니다.",
		parameters = {
			@Parameter(
				name = "fileName",
				description = "업로드할 이미지 파일명 (확장자 포함)",
				required = true,
				in = ParameterIn.QUERY,
				example = "poster-day1.jpg"
			)
		}
	)
	@GetMapping("/performances/upload-url")
	public BaseResponse<String> getPerformanceUploadUrl(
		@RequestParam
		@NotBlank(message = "파일명은 한글, 영문, 숫자, ., _, - 문자로 이루어져야 하며, 공백일 수 없으며, jpg, jpeg, png, gif, heic, webp 확장자를 포함해야 합니다.")
		@Pattern(
			regexp = "^[\\w가-힣._-]+\\.(jpg|jpeg|png|gif|heic|webp)$",
			message = "유효한 이미지 파일명이어야 합니다."
		)
		String fileName
	) {
		String uploadUrl = performanceService.getUploadUrl(fileName);
		return BaseResponse.success(uploadUrl);
	}
}
