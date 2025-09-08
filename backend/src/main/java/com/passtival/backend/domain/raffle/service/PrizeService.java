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
	public List<PrizeResponse> getAllPrizes() {
		List<Prize> prizes = prizeRepository.findAll();
		if (prizes.isEmpty()) {
			throw new BaseException(BaseResponseStatus.PRIZES_NOT_FOUND);
		}
		return prizes.stream().map(PrizeResponse::of).collect(Collectors.toList());
	}

	/**
	 * 날짜에 맞는 상품 전달하기
	 * @return List<PrizeResponse>
	 **/
	public List<PrizeResponse> getPrizeByDays(Integer days) {
		List<Prize> prizes = prizeRepository.findAllByDays(days);
		if (prizes.isEmpty()) {
			throw new BaseException(BaseResponseStatus.PRIZES_NOT_FOUND);
		}
		return prizes.stream().map(PrizeResponse::of).collect(Collectors.toList());
	}
}
