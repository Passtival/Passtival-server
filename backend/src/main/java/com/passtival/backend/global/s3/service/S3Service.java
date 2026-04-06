package com.passtival.backend.global.s3.service;

import java.net.URL;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

import com.passtival.backend.global.s3.dto.PresignedUrlResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3Service {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	@Value("${cloud.aws.s3.presigned-url.expiration-minutes:10}")
	private int expirationMinutes;

    // CloudFront 도메인
    @Value("${cloud.aws.cloudfront.domain}")
    private String cloudFrontDomain;

    // Presigned URL 생성 (업로드용 - public)
    public PresignedUrlResponse generatePresignedUrl(String directory, Long id, String originalFileName) {

        // UUID 파일명
        String uniqueFileName = UUID.randomUUID() + "_" + originalFileName;

        // objectKey
        String objectKey = String.format("public/%s/%d/%s", directory, id, uniqueFileName);

        // 만료 시간
        Date expiration = new Date(System.currentTimeMillis()
                + Duration.ofMinutes(expirationMinutes).toMillis());

        GeneratePresignedUrlRequest request =
                new GeneratePresignedUrlRequest(bucketName, objectKey)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expiration);

        URL presignedUrl = amazonS3.generatePresignedUrl(request);

        // CloudFront URL
        String fileUrl = cloudFrontDomain + "/" + objectKey;

        return new PresignedUrlResponse(
                presignedUrl.toString(),
                objectKey,
                fileUrl
        );
    }

}
