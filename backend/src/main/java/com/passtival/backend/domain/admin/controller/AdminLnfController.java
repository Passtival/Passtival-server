package com.passtival.backend.domain.admin.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.admin.model.request.FoundItemRequest;
import com.passtival.backend.domain.admin.service.AdminLnfService;
import com.passtival.backend.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/admin/found-item")
@RequiredArgsConstructor
@Tag(name = "관리자 API")
public class AdminLnfController {

	private final AdminLnfService AdminLnfService;

	@Operation(
		summary = "분실물 등록",
		description = "습득한 분실물의 정보를 등록합니다. 이미지는 사전에 업로드하고 해당 URL을 포함해야 합니다. **관리자 권한이 필요합니다.**",
		security = @SecurityRequirement(name = "jwtAuth"),
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "분실물 등록 요청 정보",
			required = true,
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = FoundItemRequest.class),
				examples = @ExampleObject(
					name = "분실물 등록 요청 예시",
					value = """
						{
						  "title": "검은색 지갑",
						  "area": "메인스테이지 앞",
						  "foundDateTime": "2024-01-15T14:30:00",
						  "imagePath": "https://bucket-name.s3.amazonaws.com/images/found/wallet-image.jpg"
						}
						"""
				)
			)
		)
	)
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public BaseResponse<Void> createFoundItem(@Valid @RequestBody FoundItemRequest request) {
		AdminLnfService.createFoundItem(request);
		return BaseResponse.success(null);
	}

	@Operation(
		summary = "분실물 삭제",
		description = "등록된 분실물을 ID로 삭제합니다. 삭제 시 관리자 인증키가 필요합니다.",
		security = @SecurityRequirement(name = "jwtAuth"),
		parameters = {
			@Parameter(
				name = "id",
				description = "삭제할 분실물의 ID",
				required = true,
				in = ParameterIn.PATH,
				example = "1"
			)
		}
	)
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public BaseResponse<Void> deleteFoundItem(@PathVariable Long id) {
		AdminLnfService.deleteFoundItem(id);
		return BaseResponse.success(null);
	}
}
