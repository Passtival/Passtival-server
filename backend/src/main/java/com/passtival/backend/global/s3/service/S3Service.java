package com.passtival.backend.global.s3.service;

import java.net.URL;
import java.time.Duration;
import java.util.Date;

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

	public String generatePresignedUrl(String fileName) {
		// S3 객체 키 생성
		String objectKey = "images/found/" + fileName;

		// Presigned Url 만료 시간 설정
		Date expiration = new Date(System.currentTimeMillis() + Duration.ofMinutes(expirationMinutes).toMillis());

		// Presigned URL 요청 생성
		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, objectKey)
			.withMethod(HttpMethod.PUT)
			.withExpiration(expiration);

		// Presigned URL 생성
		URL presignedUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

		return presignedUrl.toString();
	}

	public String getUploadUrl(String fileName) {
		return generatePresignedUrl(fileName);
	}

}
