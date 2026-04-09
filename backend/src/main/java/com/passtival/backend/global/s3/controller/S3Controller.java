package com.passtival.backend.global.s3.controller;

import com.passtival.backend.global.s3.dto.PresignedUrlResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.s3.service.S3Service;

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
@Tag(name = "S3 API", description = "S3 이미지 업로드 및 URL 관리 API")
public class S3Controller {

    private final S3Service s3Service;

    @Operation(
            summary = "이미지 업로드용 Presigned URL 발급",
            description = """
                    클라이언트가 S3에 직접 이미지를 업로드할 수 있도록 Presigned URL을 생성합니다.

                    [사용 흐름]
                    1. 이 API 호출 → uploadUrl, objectKey, fileUrl 획득
                    2. uploadUrl로 PUT 요청 → S3 업로드
                    3. objectKey를 서버로 전달 → DB 저장
                    4. fileUrl로 이미지 조회
                    """
    )
    @GetMapping("/upload-url")
    public BaseResponse<PresignedUrlResponse> getUploadUrl(

            @Parameter(
                    name = "directory",
                    description = "저장 디렉토리 (booth, activity, menu 등)",
                    example = "booth",
                    required = true,
                    in = ParameterIn.QUERY
            )
            @RequestParam
            @NotBlank(message = "디렉토리는 필수입니다.")
            String directory,

            @Parameter(
                    name = "id",
                    description = "엔티티 ID (boothId 등)",
                    example = "1",
                    required = true,
                    in = ParameterIn.QUERY
            )
            @RequestParam
            @NotNull(message = "ID는 필수입니다.")
            Long id,

            @Parameter(
                    name = "fileName",
                    description = "업로드할 파일명 (확장자 포함)",
                    example = "image.png",
                    required = true,
                    in = ParameterIn.QUERY
            )
            @RequestParam
            @NotBlank(message = "파일명은 공백일 수 없습니다.")
            @Pattern(
                    regexp = "^[\\w가-힣._-]+\\.(jpg|jpeg|png|gif|heic|webp)$",
                    message = "유효한 이미지 파일명이어야 합니다."
            )
            String fileName
    ) {
        PresignedUrlResponse response =
                s3Service.generatePresignedUrl(directory, id, fileName);

        return BaseResponse.success(response);
    }
}
