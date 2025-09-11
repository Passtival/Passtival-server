package com.passtival.backend.global.discord;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.passtival.backend.global.discord.dto.DiscordWebhookRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscordService {

	private final DiscordClient discordClient;

	@Value("${spring.profiles.active:default}")
	private String activeProfile;

	public void sendErrorNotification(String errorMessage, String requestUrl, String stackTrace) {
		try {
			DiscordWebhookRequest request = DiscordWebhookRequest.builder()
				.content(buildErrorMessage(errorMessage, requestUrl, stackTrace))
				.username("🔥 리바이")
				.build();

			discordClient.sendMessage(request);
			log.info("Discord 에러 알림 전송 완료");
		} catch (Exception e) {
			log.error("Discord 알림 전송 실패: {}", e.getMessage());
		}
	}

	private String buildErrorMessage(String errorMessage, String requestUrl, String stackTrace) {
		StringBuilder message = new StringBuilder();

		// 제목과 환경 정보
		message.append("🚨 **[서버 에러 발생]** 🚨\n");
		message.append("🌐 **환경**: `").append(activeProfile.toUpperCase()).append("`\n");
		message.append("⏰ **시간**: `")
			.append(LocalDateTime.now(ZoneId.of("Asia/Seoul")).toString().replace("T", " "))
			.append("`\n");
		message.append("🔗 **URL**: [")
			.append(requestUrl != null ? requestUrl : "Unknown")
			.append("](")
			.append(requestUrl != null ? requestUrl : "#")
			.append(")\n\n");

		// 에러 메시지 섹션
		message.append("📋 **에러 정보**\n");
		message.append("```java\n");
		message.append(formatErrorMessage(errorMessage)).append("\n");
		message.append("```\n\n");

		// 스택 트레이스 섹션 (간소화)
		if (stackTrace != null && !stackTrace.isEmpty()) {
			message.append("🔍 **주요 스택 트레이스**\n");
			message.append("```java\n");
			message.append(formatStackTrace(stackTrace)).append("\n");
			message.append("```");
		}

		// 길이 제한 (Discord 메시지 제한: 2000자)
		if (message.length() > 1950) {
			String truncated = message.substring(0, 1900);
			return truncated + "\n... (메시지 길이 제한으로 생략됨) ```";
		}

		return message.toString();
	}

	private String formatErrorMessage(String errorMessage) {
		if (errorMessage == null || errorMessage.isEmpty()) {
			return "알 수 없는 에러";
		}

		// 에러 메시지에서 패키지명 간소화
		return errorMessage
			.replace("com.passtival.backend.", "")
			.replace("java.lang.", "");
	}

	private String formatStackTrace(String stackTrace) {
		if (stackTrace == null || stackTrace.isEmpty()) {
			return "스택 트레이스 없음";
		}

		String[] lines = stackTrace.split("\n");
		StringBuilder formatted = new StringBuilder();
		int projectCount = 0;
		int otherCount = 0;

		// 프로젝트 스택 우선 표시
		for (String line : lines) {
			if (line.contains("com.passtival.backend") && projectCount < 3) {
				formatted.append(String.format("%d. → %s\n",
					projectCount + 1,
					line.trim()
						.replace("com.passtival.backend.", "")
						.replace("\tat ", "")));
				projectCount++;
			}
		}

		// 프로젝트 스택이 적으면 다른 스택도 표시
		if (projectCount < 2) {
			for (String line : lines) {
				if (!line.contains("com.passtival.backend") &&
					otherCount < (3 - projectCount) &&
					!line.trim().isEmpty() &&
					!line.contains("java.base/") && // JVM 내부 스택 제외
					!line.contains("org.springframework.cglib")) { // CGLIB 스택 제외

					formatted.append(String.format("%d. → %s\n",
						projectCount + otherCount + 1,
						line.trim().replace("\tat ", "")));
					otherCount++;
				}
			}
		}

		// 로그 참조 안내 추가
		formatted.append("\n💡 전체 스택 트레이스는 서버 로그 확인");

		return formatted.toString();
	}
}