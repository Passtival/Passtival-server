package com.passtival.backend.domain.authenticationkey.controller.test;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.authenticationkey.repository.AuthenticationKeyRepository;
import com.passtival.backend.domain.authenticationkey.service.AuthenticationKeyImportService;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
		description = "요청한 row 수만큼 인증키 엑셀 import를 수동 실행합니다. (실제 읽기 수 = min(요청값, 엑셀 행 수))"
	)
	@PostMapping("/import")
	public BaseResponse<ImportResult> importAuthenticationKeys(@Valid @RequestBody ImportRequest request) {
		try {
			long beforeCount = authenticationKeyRepository.count();
			long startNanos = System.nanoTime();

			authenticationKeyImportService.importXlsx(request.getRowsToImport());

			long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000;
			long afterCount = authenticationKeyRepository.count();
			long insertedCount = afterCount - beforeCount;

			return BaseResponse.success(
				ImportResult.builder()
					.requestedRows(request.getRowsToImport())
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
		private int requestedRows;
		private long beforeCount;
		private long afterCount;
		private long insertedCount;
		private long elapsedMs;
	}

	@Getter
	public static class ImportRequest {
		@NotNull
		@Min(1)
		private Integer rowsToImport;
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
