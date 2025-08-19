package com.passtival.backend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class AwsConfig {

	/**
	 * AWS S3 클라이언트 설정
	 */

	@org.springframework.beans.factory.annotation.Value("${cloud.aws.credentials.access-key}")
	private String accessKey;

	@org.springframework.beans.factory.annotation.Value("${cloud.aws.credentials.secret-key}")
	private String secretKey;

	@Value("${cloud.aws.region.static}")
	private String region;

	@Bean
	public AmazonS3 amazonS3() {
		BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

		return AmazonS3ClientBuilder.standard()
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.withRegion(Regions.fromName(region))
			.build();
	}
}
