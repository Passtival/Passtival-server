package com.passtival.backend.global.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedUrlResponse {

    private String uploadUrl;   // S3 업로드용
    private String objectKey;   // DB 저장용
    private String fileUrl;     // CloudFront 조회용
}
