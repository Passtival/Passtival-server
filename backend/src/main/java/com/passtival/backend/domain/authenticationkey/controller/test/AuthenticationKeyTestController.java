package com.passtival.backend.domain.authenticationkey.controller.test;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.authenticationkey.repository.AuthenticationKeyRepository;
import com.passtival.backend.domain.authenticationkey.service.AuthenticationKeyImportService;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/test/authentication-keys")
@RequiredArgsConstructor
@Tag(name = "Test-API", description = "[백엔드 용] 테스트용 인증키 API")
public class AuthenticationKeyTestController {

	private final AuthenticationKeyImportService authenticationKeyImportService;
	private final AuthenticationKeyRepository authenticationKeyRepository;

	@Operation(
		summary = "인증키 엑셀 수동 import",
		description = "스프링 시작 시 자동 시드와 별개로, 테스트를 위해 인증키 엑셀 import를 수동 실행합니다."
	)
	@PostMapping("/import")
	public BaseResponse<ImportResult> importAuthenticationKeys() {
		try {
			long beforeCount = authenticationKeyRepository.count();
			long startNanos = System.nanoTime();

			authenticationKeyImportService.importXlsx();

			long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000;
			long afterCount = authenticationKeyRepository.count();
			long insertedCount = afterCount - beforeCount;

			return BaseResponse.success(
				ImportResult.builder()
					.beforeCount(beforeCount)
					.afterCount(afterCount)
					.insertedCount(insertedCount)
					.elapsedMs(elapsedMs)
					.build()
			);
		} catch (Exception e) {
			return BaseResponse.fail(BaseResponseStatus.INTERNAL_SERVER_ERROR, "인증키 import 실패: " + e.getMessage());
		}
	}

	@Operation(
		summary = "전체 인증키 삭제",
		description = "테스트를 위해 authentication_key 테이블의 전체 데이터를 삭제합니다."
	)
	@DeleteMapping("/all")
	public BaseResponse<DeleteResult> deleteAllAuthenticationKeys() {
		try {
			long beforeCount = authenticationKeyRepository.count();
			long startNanos = System.nanoTime();

			authenticationKeyRepository.deleteAllInBatch();

			long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000;
			long afterCount = authenticationKeyRepository.count();

			return BaseResponse.success(
				DeleteResult.builder()
					.beforeCount(beforeCount)
					.afterCount(afterCount)
					.deletedCount(beforeCount - afterCount)
					.elapsedMs(elapsedMs)
					.build()
			);
		} catch (Exception e) {
			return BaseResponse.fail(BaseResponseStatus.INTERNAL_SERVER_ERROR, "인증키 전체 삭제 실패: " + e.getMessage());
		}
	}

	@Getter
	@Builder
	public static class ImportResult {
		private long beforeCount;
		private long afterCount;
		private long insertedCount;
		private long elapsedMs;
	}

	@Getter
	@Builder
	public static class DeleteResult {
		private long beforeCount;
		private long afterCount;
		private long deletedCount;
		private long elapsedMs;
	}
}
