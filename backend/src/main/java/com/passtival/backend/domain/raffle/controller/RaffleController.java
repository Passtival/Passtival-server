package com.passtival.backend.domain.raffle.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.domain.raffle.model.entity.Prize;
import com.passtival.backend.domain.raffle.model.request.ApplicantRegistrationRequest;
import com.passtival.backend.domain.raffle.model.request.UpdateAuthenticationKeyRequest;
import com.passtival.backend.domain.raffle.model.response.PrizeResponse;
import com.passtival.backend.domain.raffle.service.PrizeService;
import com.passtival.backend.domain.raffle.service.RaffleService;
import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/raffle")
@RequiredArgsConstructor
public class RaffleController {

	private final RaffleService raffleService;
	private final PrizeService prizeService;

	/**
	 * 상품 목록 조회 API
	 * @return 상품 목록 응답
	 */
	@GetMapping("/prizes")
	public BaseResponse<List<PrizeResponse>> getPrizes() {
		try {
			List<Prize> prizes = prizeService.getAllPrizes();
			List<PrizeResponse> prizeResponses = prizes.stream()
				.map(PrizeResponse::of)
				.collect(Collectors.toList());
			return BaseResponse.success(prizeResponses);

		} catch (Exception e) {
			log.error("상품 목록 조회 실패", e);
			return BaseResponse.fail(BaseResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 상품 ID로 상품 조회 API
	 * @param prizeId 상품 ID
	 * @return 상품 정보 응답
	 */
	@GetMapping("/prizes/{prizeId}")
	public BaseResponse<PrizeResponse> getPrizeById(@PathVariable("prizeId") Long prizeId) {
		try {
			Prize prize = prizeService.getPrizeById(prizeId);
			PrizeResponse prizeResponse = PrizeResponse.of(prize);
			return BaseResponse.success(prizeResponse);

		} catch (RuntimeException e) {
			log.warn("상품 조회 실패 - prizeId: {}, 사유: {}", prizeId, e.getMessage());
			return BaseResponse.fail(BaseResponseStatus.BAD_REQUEST, e.getMessage());
		} catch (Exception e) {
			log.error("상품 조회 중 예상치 못한 오류 - prizeId: {}", prizeId, e);
			return BaseResponse.fail(BaseResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 신청자 등록 API
	 * @param request 신청자 등록 요청 정보
	 * @return 신청자 등록 응답 정보
	 */
	@PostMapping("/applicants")
	public BaseResponse<Void> saveApplicant(@RequestBody ApplicantRegistrationRequest request) {
		/* 1. 클라이언트로부터 신청자의 이름과 학번, 인증키 입력받는다.
		* 2. 신청자의 이름과 학번을 기반으로 신청자를 데이터베이스에 저장한다.
		* 3. 신청이 완료되면 클라이언트에게 성공 메시지를 반환한다.
		* 4. 만약 신청자의 이름과 학번이 같은 경우, 이미 신청한 것으로 간주하고 에러 메시지를 반환한다.
		 */
		try {
			raffleService.registerApplicant(request);
			return BaseResponse.success(null);
		} catch (RuntimeException e) {
			log.warn("신청자 등록 실패 - name: {}, studentId: {}, 사유: {}",
				request.getApplicantName(), request.getStudentId(), e.getMessage());
			return BaseResponse.fail(BaseResponseStatus.BAD_REQUEST, e.getMessage());
		} catch (Exception e)  {
			log.error("신청자 등록 중 예상치 못한 오류", e);
			return BaseResponse.fail(BaseResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}


	/**
	 * 인증키 변경 API
	 * @param request 인증키 변경 요청 정보(oldKey, newKey)
	 * @return 변경 완료 응답
	 */
	@PutMapping("/authentication-key")
	public BaseResponse<Void> updateAuthenticationKey(@Valid @RequestBody UpdateAuthenticationKeyRequest request) {
		try {
			raffleService.updateAuthenticationKey(request.getNewKey(), request.getOldKey());
			return BaseResponse.success(null);
		} catch (RuntimeException e) {
			log.warn("인증키 변경 실패 - 사유: {}", e.getMessage());
			return BaseResponse.fail(BaseResponseStatus.BAD_REQUEST, e.getMessage());
		} catch (Exception e) {
			log.error("인증키 변경 중 예상치 못한 오류", e);
			return BaseResponse.fail(BaseResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
