package com.passtival.backend.domain.lostfound.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.lostfound.model.response.FoundItemResponse;
import com.passtival.backend.domain.lostfound.service.LnfService;
import com.passtival.backend.global.common.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/found-items")
@Tag(name = "분실물 관련 API", description = "분실물 등록, 조회, 삭제 API")
public class LnfController {

	private final LnfService lnfService;

	@Operation(
		summary = "분실물 상세 조회",
		description = "ID로 등록된 분실물의 상세 정보를 조회합니다.",
		parameters = {
			@Parameter(
				name = "id",
				description = "조회할 분실물의 ID",
				required = true,
				in = ParameterIn.PATH,
				example = "1"
			)
		}
	)
	@GetMapping("/{id}")
	public BaseResponse<FoundItemResponse> getFoundItemById(@PathVariable Long id) {
		FoundItemResponse response = lnfService.getFoundItemById(id);
		return BaseResponse.success(response);
	}

	@Operation(
		summary = "모든 분실물 조회",
		description = "등록된 모든 분실물의 정보를 조회합니다."
	)
	@GetMapping
	public BaseResponse<List<FoundItemResponse>> getAllFoundItems() {
		List<FoundItemResponse> responses = lnfService.getAllFoundItems();
		return BaseResponse.success(responses);
	}

}
