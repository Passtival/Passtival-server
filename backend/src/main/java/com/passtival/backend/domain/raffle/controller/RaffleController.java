// package com.passtival.backend.domain.raffle.controller;
//
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
//
// import com.passtival.backend.domain.raffle.model.request.ApplicantRegistrationRequest;
// import com.passtival.backend.domain.raffle.service.RaffleService;
// import com.passtival.backend.global.common.BaseResponse;
//
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.media.Content;
// import io.swagger.v3.oas.annotations.media.ExampleObject;
// import io.swagger.v3.oas.annotations.media.Schema;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
//
// @Slf4j
// @RestController
// @RequestMapping("/api/raffle")
// @RequiredArgsConstructor
// @Tag(name = "응모권 관련 API", description = "상품 조회, 신청자 등록")
// public class RaffleController {
//
// 	private final RaffleService raffleService;
//
// 	@Operation(
// 		summary = "신청자 등록",
// 		description = "신청자의 이름과 학번, 인증키를 입력받아 신청자를 등록합니다.",
// 		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
// 			description = "신청자 등록 요청 정보",
// 			required = true,
// 			content = @Content(
// 				mediaType = "application/json",
// 				schema = @Schema(implementation = ApplicantRegistrationRequest.class),
// 				examples = @ExampleObject(
// 					name = "신청자 등록 요청 예시",
// 					value = """
// 						{
// 						  "applicantName": "박준선",
// 						  "studentId": "2021U2317",
// 						  "authenticationKey": "1234"
// 						}
// 						"""
// 				)
// 			)
// 		)
// 	)
// 	@PostMapping("/applicants")
// 	public BaseResponse<Void> saveApplicant(@Valid @RequestBody ApplicantRegistrationRequest request) {
// 		/* 1. 클라이언트로부터 신청자의 이름과 학번, 인증키 입력받는다.
// 		 * 2. 신청자의 이름과 학번을 기반으로 신청자를 데이터베이스에 저장한다.
// 		 * 3. 신청이 완료되면 클라이언트에게 성공 메시지를 반환한다.
// 		 * 4. 만약 신청자의 이름과 학번이 같은 경우, 이미 신청한 것으로 간주하고 에러 메시지를 반환한다.
// 		 */
// 		raffleService.registerApplicant(request);
// 		return BaseResponse.success(null);
// 	}
// }
