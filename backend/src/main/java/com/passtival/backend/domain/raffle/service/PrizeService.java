package com.passtival.backend.domain.raffle.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.passtival.backend.domain.raffle.model.entity.Prize;
import com.passtival.backend.domain.raffle.model.response.PrizeResponse;
import com.passtival.backend.domain.raffle.repository.PrizeRepository;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrizeService {

	private final PrizeRepository prizeRepository;

	/**
	 * 상품 목록 조회
	 * @return 상품 목록
	 */
	public List<PrizeResponse> getAllPrizes() throws BaseException {
		List<Prize> prizes = prizeRepository.findAll();
		if (prizes.isEmpty()) {
			throw new BaseException(BaseResponseStatus.PRIZES_NOT_FOUND);
		}
		return prizes.stream().map(PrizeResponse::of).collect(Collectors.toList());
	}

	/**
	 * 상품 ID로 상품 조회
	 * @param prizeId
	 * @return 상품 정보
	 */
	public PrizeResponse getPrizeById(Long prizeId) throws BaseException {
		Prize prize = prizeRepository.findById(prizeId)
			.orElseThrow(() -> new BaseException(BaseResponseStatus.PRIZE_NOT_FOUND));
		return PrizeResponse.of(prize);
	}

}
