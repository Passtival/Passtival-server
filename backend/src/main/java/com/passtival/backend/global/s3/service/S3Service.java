package com.passtival.backend.global.s3.service;

import java.net.URL;
import java.time.Duration;
import java.util.Date;
import java.time.LocalDate;
import java.util.UUID;

import com.passtival.backend.global.s3.dto.PresignedUrlResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

@Service
public class S3Service {

    private final AmazonS3 amazonS3;
    private final String bucketName;
    private final int expirationMinutes;
    private final String cloudFrontDomain;

    public S3Service(
            AmazonS3 amazonS3,
            @Value("${cloud.aws.s3.bucket}") String bucketName,
            @Value("${cloud.aws.s3.presigned-url.expiration-minutes:10}") int expirationMinutes,
            @Value("${cloud.aws.cloudfront.domain}") String cloudFrontDomain
    ) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
        this.expirationMinutes = expirationMinutes;
        this.cloudFrontDomain = cloudFrontDomain;
    }

    public PresignedUrlResponse generatePresignedUrl(String directory, Long id, String originalFileName) {

        // UUID 파일명
        String uniqueFileName = UUID.randomUUID() + "_" + originalFileName;

        // 🔥 현재 연도 추가
        String year = String.valueOf(LocalDate.now().getYear());

        // objectKey (year 포함)
        String objectKey = String.format("public/%s/%s/%d/%s",
                year, directory, id, uniqueFileName);

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
